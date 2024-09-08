package com.example.attendanceapp.ui.admin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.attendanceapp.R
import com.example.attendanceapp.data.AppDatabase
import com.example.attendanceapp.data.entities.Attendance
import com.example.attendanceapp.ui.Adapter.StudentReportAdapter
import com.example.attendanceapp.ui.Adapter.UserAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class StudentReportActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var stdList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_report)
        db = AppDatabase.getDatabase(this)
        stdList = findViewById(R.id.studentRcv2)
    }

    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.IO).launch {
            val users = db.userDao().getAllUsers().toCollection(ArrayList())
            val attendanceList = db.attendanceDao().getAllAttendances().toCollection(ArrayList())// Ensure this method exists

            withContext(Dispatchers.Main) {
                stdList.layoutManager = LinearLayoutManager(this@StudentReportActivity, LinearLayoutManager.VERTICAL, false)
                stdList.adapter = StudentReportAdapter(users, attendanceList,) // Pass lists without casting
            }
        }
    }


}
