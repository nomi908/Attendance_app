package com.example.attendanceapp.ui.admin

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.attendanceapp.R
import com.example.attendanceapp.data.AppDatabase
import com.example.attendanceapp.data.entities.Attendance
import com.example.attendanceapp.data.entities.User
import com.example.attendanceapp.ui.Adapter.UserAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class StudentRecordSearch : AppCompatActivity() {

    private lateinit var startDate: EditText
    private lateinit var endDate: EditText
    private lateinit var emailtxt : TextView
    private lateinit var searchButton: Button
    private lateinit var resultList: ListView
    private lateinit var db: AppDatabase
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private lateinit var presentDays : TextView
    private lateinit var attdGrade : TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_record_search)

        startDate = findViewById(R.id.startDate)
        endDate = findViewById(R.id.endDate)
        emailtxt = findViewById(R.id.useremailtxt)
        searchButton = findViewById(R.id.search_button)
        resultList = findViewById(R.id.resultliststd)
        attdGrade = findViewById(R.id.attdGrade)
        presentDays = findViewById(R.id.presentDays)

        val username = intent.getStringExtra("username")
        val email = intent.getStringExtra("useremail")
        val profilePicture = intent.getStringExtra("profilepicture")
        val attendanceStatus = intent.getStringExtra("attdstatus").toString()
        val attendanceDate = intent.getStringExtra("chkindate").toString()
        val attendanceId = intent.getIntExtra("userid", 0)
        db = AppDatabase.getDatabase(this)

//        Log.d("email2", "onCreate: "+ email)

        startDate.setOnClickListener {
            showDatePickerDialog(startDate)
        }

        endDate.setOnClickListener {
            showDatePickerDialog(endDate)
        }
         emailtxt.text = email
        searchButton.setOnClickListener {
            Toast.makeText(this, "button pressed", Toast.LENGTH_SHORT).show()

            val start = startDate.text.toString()
            val end = endDate.text.toString()

            if (email!!.isNotBlank() && start.isNotBlank() && end.isNotBlank()) {

                searchAttendancesByDateAndEmail(start, end, attendanceId!!)
            } else {
                displayMessage("Please fill in all fields.")
            }
        }
    }

    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance().apply {
                    set(Calendar.YEAR, selectedYear)
                    set(Calendar.MONTH, selectedMonth)
                    set(Calendar.DAY_OF_MONTH, selectedDay)
                }.time
                editText.setText(dateFormat.format(selectedDate))
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun searchAttendancesByDateAndEmail(startDate: String, endDate: String, attendanceId: Int) {
        CoroutineScope(Dispatchers.IO).launch {

            Log.d("user_data", "userId: " + attendanceId)
            Log.d("user_data", "startDate: " + startDate)
            Log.d("user_data", "endDate: " + endDate)
            val records = db.attendanceDao().searchAttendancesByDateRangeAndUserId(startDate, endDate, attendanceId)
            Log.d("recors", "searchAttendancesByDateAndEmail: "+ records)

            val daysAttended = records.size // Count the number of days attended
            val grade = determineGrade(daysAttended)
            presentDays.text = daysAttended.toString()
            withContext(Dispatchers.Main) {
                displayResults(records, grade)
            }
        }
    }

//    private fun searchAttendancesByDateAndEmail(startDate: String, endDate: String, email: String) {
//        CoroutineScope(Dispatchers.IO).launch {
//            Log.d("Searching", "email = $email")
//
//            val user = db.userDao().searchUsers(email)
//            Log.d("Searching", "user = $user")
//            val records = user?.let {
//                db.attendanceDao().searchAttendancesByDateRangeAndUserId(startDate, endDate, it.get(0).id)
//
//            } ?: emptyList()
//            Log.d("redo", "searchAttendancesByDateAndEmail: "+ records)
//
//            withContext(Dispatchers.Main) {
//                displayResults(records)
//            }
//        }
//    }

    private fun displayResults(results: List<Attendance>, grade:String) {
        Log.d("dataapt", "displayResults: "+ results)

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            results.map { "${it.date} - ${it.status}" } // Customize display format as needed
        )
        resultList.adapter = adapter
        attdGrade.setText(grade)
    }

    private fun displayMessage(message: String) {
        // Handle displaying messages to the user, e.g., Toast or a TextView
    }
    private fun determineGrade(daysAttended: Int): String {
        return when {
            daysAttended >= 26 -> "A"
            daysAttended in 21..25 -> "B"
            daysAttended in 16..20 -> "C"
            daysAttended in 10..15 -> "D"
            else -> "F"
        }
    }
}
