package com.example.attendanceapp.ui.user

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.attendanceapp.R
import com.example.attendanceapp.data.AppDatabase
import com.example.attendanceapp.data.entities.Attendance
import com.example.attendanceapp.data.entities.User
import com.example.attendanceapp.ui.registration.RegistrationActivity
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID


class UserPanelActivity : AppCompatActivity() {
    private lateinit var mkatd: Button
    private lateinit var mkleave: Button
    private lateinit var db: AppDatabase
    private lateinit var btnview :Button
    private lateinit var imagechoose :LinearLayout
    private lateinit var avatarImg :ImageView
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private var selectedImageUri: Uri? = null
    private lateinit var logoutbtn :Button






        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_panel)

        db = AppDatabase.getDatabase(this)
        mkatd = findViewById(R.id.mkatd)
        mkleave = findViewById(R.id.mkleave)
        btnview = findViewById(R.id.btnview)
        imagechoose = findViewById(R.id.imagechoose)
        avatarImg = findViewById(R.id.avatarImg)
        logoutbtn = findViewById(R.id.logoutbtn)


        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                selectedImageUri = data?.data
                selectedImageUri?.let { uri ->
                    Glide.with(this).load(uri).into(avatarImg)
                    // Save image to internal storage and update database
                    saveImageToDatabase(uri)

                }
            }
        }


        mkatd.setOnClickListener {
            showDialogbox()

        }

        mkleave.setOnClickListener {
            startActivity(Intent(this@UserPanelActivity, LeaveRequestActivity::class.java))
        }
        btnview.setOnClickListener {
            startActivity(Intent(this@UserPanelActivity, ViewAttendanceRecord::class.java))

        }
        imagechoose.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            imagePickerLauncher.launch(intent)
        }

        loadUserProfileImage()

       logoutbtn.setOnClickListener {

           val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
           with(sharedPreferences.edit()) {
               clear() // This will remove all key-value pairs
               apply() // Save changes asynchronously
//               finish()
           }

           // Optionally, you can log out of any other services here

           // Redirect to LoginActivity
           val intent = Intent(this@UserPanelActivity, RegistrationActivity::class.java)
           startActivity(intent)
           finish() // Finish the current activity to remove it from the back stack
       }


       }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun saveImageToDatabase(imageUri: Uri) {
        lifecycleScope.launch {
            try {
                val userId = getUserId() // Get the stored user ID

                if (userId == -1) {
                    // User is not logged in or ID is not available
                    Toast.makeText(this@UserPanelActivity, "User not logged in", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // Save image to internal storage
                val imagePath = saveImageToInternalStorage(imageUri )

                // Update user profile with image path or URI
                db.userDao().updateProfilePicture(userId, imagePath)

                Toast.makeText(this@UserPanelActivity, "Image saved successfully!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@UserPanelActivity, "Failed to save image: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveImageToInternalStorage(imageUri: Uri): String {
        val inputStream = contentResolver.openInputStream(imageUri) ?: return ""
        val uniqueId = UUID.randomUUID().toString()
        val file = File(filesDir, "profile_image${uniqueId}.jpg")
        val outputStream = FileOutputStream(file)

        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()

        return file.absolutePath
    }
    private fun loadUserProfileImage() {
        lifecycleScope.launch {
            val userId = getUserId()
            val user = db.userDao().getUserById(userId)
            user?.profilePicture?.let { imagePath ->
                Glide.with(this@UserPanelActivity).load(File(imagePath)).into(avatarImg)
            }
        }
    }



    private fun showDialogbox() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Mark Attendance")
        builder.setMessage("Do you want to mark your attendance today?")

        builder.setPositiveButton("Yes") { dialog, _ ->
            markAttendance()
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()


    }



    private fun markAttendance() {
        lifecycleScope.launch {
            try {

                val userId = getUserId() // Get the stored user ID

                if (userId == -1) {
                    // User is not logged in or ID is not available
                    Toast.makeText(this@UserPanelActivity, "User not logged in", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

                val existingRecords = db.attendanceDao().getAttendanceByUserId(userId, currentDate)
                if (existingRecords.isNotEmpty()) {
                    // Attendance already marked for today
                    Toast.makeText(this@UserPanelActivity, "Attendance already marked for today!", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val currentDateTime =
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format((Date()))
                val attendance = Attendance(
                    userId = userId,
                    date = currentDateTime,
                    status = "Present"
                )
                db.attendanceDao().insert(attendance)

                Toast.makeText(
                    this@UserPanelActivity,
                    "Attendance marked successfully!",
                    Toast.LENGTH_SHORT
                ).show()

            } catch (e: Exception) {
                Toast.makeText(
                    this@UserPanelActivity,
                    "Failed to mark attendance: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    // Function to retrieve user ID from SharedPreferences
    private fun getUserId(): Int {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("user_id", -1) // Return -1 if user ID not found
    }

}