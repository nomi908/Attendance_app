package com.example.attendanceapp.ui.user

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.attendanceapp.R
import com.example.attendanceapp.data.AppDatabase
import com.example.attendanceapp.data.entities.Leave
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class LeaveRequestActivity : AppCompatActivity() {
    private lateinit var startDate: EditText
    private lateinit var endDate: EditText
    private lateinit var txtsubject: EditText
    private lateinit var txtlevreason: EditText
    private lateinit var db: AppDatabase
    private lateinit var applyFromdate :TextView
    private lateinit var applyTodate : TextView
    private lateinit var resultlayout : LinearLayout
    private lateinit var mainlayout : LinearLayout
    private lateinit var leavestatus :TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leave_request)

        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userID = sharedPreferences.getInt("user_Id", -1)
        db = AppDatabase.getDatabase(this)

        startDate = findViewById(R.id.startDatetxt)
        endDate = findViewById(R.id.endDatetxt)
        txtsubject = findViewById(R.id.txtsubject)
        txtlevreason = findViewById(R.id.txtlevreason)
        applyTodate = findViewById(R.id.applyTodate)
        applyFromdate = findViewById(R.id.applyFromdate)
        resultlayout = findViewById(R.id.resultlayout)
        mainlayout = findViewById(R.id.mainlayout)
        leavestatus = findViewById(R.id.leavestatus)


        // Check if a leave request has been made
        val hasApplied = sharedPreferences.getBoolean("has_applied", false)
        if (hasApplied) {
            // Show result layout and hide main layout
            mainlayout.visibility = LinearLayout.GONE
            resultlayout.visibility = LinearLayout.VISIBLE
            leavestatus.setText("Leave Status Pending")

            // Load and display leave details
            CoroutineScope(Dispatchers.IO).launch {
                val leave = db.leaveDao().getLeavesByUserId(userID)
                runOnUiThread {
                    applyFromdate.text = leave?.fromDate ?: ""
                    applyTodate.text = leave?.toDate ?: ""
                }
            }
        } else {
            // Show main layout and hide result layout
            mainlayout.visibility = LinearLayout.VISIBLE
            resultlayout.visibility = LinearLayout.GONE
        }




        startDate.setOnClickListener {
            showDatePickerDialog(startDate)
        }
        endDate.setOnClickListener {
            showDatePickerDialog(endDate)
        }

        val applyButton = findViewById<Button>(R.id.btnapply)
        applyButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                db.leaveDao().insert(
                    Leave(
                        0,
                        userID,
                        toDate = endDate.text.toString(),
                        fromDate = startDate.text.toString(),
                        subject = txtsubject.text.toString(),
                        levreason = txtlevreason.text.toString(),
                        status = "pending"
                    )
                )
                // Update shared preferences to indicate the user has applied
                sharedPreferences.edit().putBoolean("has_applied", true).apply()


                runOnUiThread {
                    // Update the UI to show the applied leave details
                    applyFromdate.text = startDate.text.toString()
                    applyTodate.text = endDate.text.toString()

                    // Hide the leave request form and show the result layout
                    findViewById<LinearLayout>(R.id.mainlayout).visibility = LinearLayout.GONE
                    resultlayout.visibility = LinearLayout.VISIBLE
                    leavestatus.setText("Leave Status Pending")

                }


            }


        }

    }

    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                // Format the selected date
                val selectedDate = Calendar.getInstance().apply {
                    set(selectedYear, selectedMonth, selectedDay)
                }.time
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate)

                // Set the formatted date to the EditText
                editText.setText(formattedDate)
            }, year, month, day)

        datePickerDialog.show()
    }
}