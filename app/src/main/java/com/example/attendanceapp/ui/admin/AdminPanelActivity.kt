package com.example.attendanceapp.ui.admin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.attendanceapp.R
import com.example.attendanceapp.data.AppDatabase
import com.example.attendanceapp.data.dao.UserDao
import com.example.attendanceapp.data.entities.Attendance
import com.example.attendanceapp.data.entities.User
import com.example.attendanceapp.ui.registration.RegistrationActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminPanelActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_panel)
        db = AppDatabase.getDatabase(this)
        val leaveCard = findViewById<CardView>(R.id.leaveCard)
        val allstdCard = findViewById<CardView>(R.id.allstdCard)
        val logoutbutton = findViewById<Button>(R.id.logoutbutton)
        val searchstd = findViewById<CardView>(R.id.SrchSTD)

        leaveCard.setOnClickListener {
            startActivity(Intent(this, Leave_StdList_Activity::class.java))
        }
        allstdCard.setOnClickListener {
            fetchUsers()
        }

        logoutbutton.setOnClickListener {
            val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            with(sharedPreferences.edit()) {
                clear() // This will remove all key-value pairs
                apply() // Save changes asynchronously
            }
            // Redirect to LoginActivity
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
            finish() // Finish the current activity to remove it from the back stack
        }
        searchstd.setOnClickListener {
            startActivity(Intent(this, StudentReportActivity::class.java))

                }



    }

    private fun fetchUsers() {

            val intent = Intent(this@AdminPanelActivity, All_Studenlist_Activity::class.java)
            // Convert list to a serializable form if needed
            startActivity(intent)

    }
}





