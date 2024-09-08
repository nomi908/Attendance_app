package com.example.attendanceapp.ui.user

import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.attendanceapp.R
import com.example.attendanceapp.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ViewAttendanceRecord : AppCompatActivity() {
    private lateinit var db: AppDatabase
    private lateinit var attendanceListView: ListView
    private var userId :Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_attendance_record)
        db = AppDatabase.getDatabase(this)

        attendanceListView = findViewById(R.id.attendanceListview)

        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getInt("user_id", -1)

        if (userId != -1) {
            val currentMonthPrefix = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())

            CoroutineScope(Dispatchers.IO).launch {
                val attendanceList = db.attendanceDao().getAttendanceByUserId(userId!!, currentMonthPrefix)
                runOnUiThread {
                    val adapter = ArrayAdapter(
                        this@ViewAttendanceRecord,
                        android.R.layout.simple_list_item_1,
                        attendanceList.map { "${it.date}: ${it.status}" }
                    )
                    attendanceListView.adapter = adapter
                }
            }
        } else {
            // Handle the case where userId is not valid
            runOnUiThread {
                Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show()
            }
        }

    }
}