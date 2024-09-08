package com.example.attendanceapp.ui.admin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.attendanceapp.R
import com.example.attendanceapp.data.AppDatabase
import com.example.attendanceapp.data.entities.Attendance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.log

class UserDetailsActivity : AppCompatActivity() {
    private lateinit var uemail: TextView
    private lateinit var uname: TextView
    private lateinit var userdtimg: ImageView
    private lateinit var atdstatus: TextView
    private lateinit var editbtn: Button
    private lateinit var deletebtn: Button
    private lateinit var datetime: TextView
    private lateinit var db: AppDatabase
    private var attendanceId: Int = 0  // Store the ID of the attendance record


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)

        val username = intent.getStringExtra("username")
        val email = intent.getStringExtra("useremail")
        val profilePicture = intent.getStringExtra("profilepicture")
        val attendanceStatus = intent.getStringExtra("attdstatus").toString()
        val attendanceDate = intent.getStringExtra("chkindate").toString()
        attendanceId = intent.getIntExtra("userid", 0)
        db = AppDatabase.getDatabase(this)

        uemail = findViewById(R.id.uemail)
        uname = findViewById(R.id.uname)
        userdtimg = findViewById(R.id.userdtimg)
        atdstatus = findViewById(R.id.atdstatus)
        editbtn = findViewById(R.id.editbtn)
        deletebtn = findViewById(R.id.deletebtn)
        datetime = findViewById(R.id.datetime)


        uname.text = username
        uemail.text = email

        if (attendanceStatus == "Present") {
            atdstatus.text = attendanceStatus
        } else {
            atdstatus.text = "Absent"
        }

        datetime.text = attendanceDate

        Glide.with(this)
            .load(profilePicture)
            .placeholder(R.drawable.ic_user)
            .into(userdtimg)


        editbtn.setOnClickListener {
            showDialogEdit()


        }
        deletebtn.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Delete Record")
                .setMessage("Are you sure you want to delete this attendance record?")
                .setPositiveButton("Yes") { _, _ ->
                    deleteUser()
                }
                .setNegativeButton("No", null)
                .show()
        }


    }

    private fun showDialogEdit() {
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.dialog_edit, null)
        val statusSpinner = dialogView.findViewById<Spinner>(R.id.attendance_status_spinner)


        statusSpinner.setSelection(if (atdstatus.text == "Present") 0 else 1)
        AlertDialog.Builder(this)
            .setTitle("Edit Attendance")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val newStatus = statusSpinner.selectedItem.toString()
                val updateDate = getCurrentDate()
                Log.d("Date", "showDialogEdit: " + updateDate)
                Log.d("newstatus", "showDialogEdit: " + newStatus)
                Log.d("attendanceID", "showDialogEdit: " + attendanceId)
                val updateattendance =
                    Attendance(status = newStatus, userId = attendanceId, date = updateDate)

                CoroutineScope(Dispatchers.IO).launch {

                    db.attendanceDao().updateAttendance(attendanceId, newStatus, updateDate)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@UserDetailsActivity,
                            "Attendance updated successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        // Update the UI with new data

                        atdstatus.text = newStatus
                        datetime.text = updateDate


                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault())
        return sdf.format(System.currentTimeMillis())
    }

    private fun deleteUser() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                db.attendanceDao().delete(attendanceId)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@UserDetailsActivity, "Attendance deleted successfully", Toast.LENGTH_SHORT).show()
                    finish()  // Close the activity if needed
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@UserDetailsActivity, "Failed to delete attendance: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}