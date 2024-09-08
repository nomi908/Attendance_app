package com.example.attendanceapp.ui.admin

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.attendanceapp.R
import com.example.attendanceapp.data.AppDatabase
import com.example.attendanceapp.data.entities.Leave
import com.example.attendanceapp.ui.Adapter.LeaveRequestAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//class Leave_StdList_Activity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_leave_std_list)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//    }
//}
class Leave_StdList_Activity : AppCompatActivity() {

    private lateinit var leaveRequestAdapter: LeaveRequestAdapter
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_leave_std_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = AppDatabase.getDatabase(this)

        val recyclerView = findViewById<RecyclerView>(R.id.leaveRcv)
        recyclerView.layoutManager = LinearLayoutManager(this)

        leaveRequestAdapter = LeaveRequestAdapter(
            emptyList(),
            userDao = db.userDao(),  // Pass UserDao
            onApproveClick = { leave -> handleApprove(leave) },
            onRejectClick = { leave -> handleReject(leave) }
        )
        recyclerView.adapter = leaveRequestAdapter

        fetchLeaveRequests()
    }

    private fun fetchLeaveRequests() {
        CoroutineScope(Dispatchers.IO).launch {
            val leaveEntities = db.leaveDao().getAllLeaves() // Ensure this method exists in your DAO
            withContext(Dispatchers.Main) {
                leaveRequestAdapter.updateLeaveRequests(leaveEntities)
            }
        }
    }

    private fun handleApprove(leave: Leave) {
        CoroutineScope(Dispatchers.IO).launch {
            db.leaveDao().updateLeaveStatus(leave.id, "Approved")
            fetchLeaveRequests() // Refresh the list
        }
    }

    private fun handleReject(leave: Leave) {
        CoroutineScope(Dispatchers.IO).launch {
            db.leaveDao().updateLeaveStatus(leave.id, "Rejected")
            fetchLeaveRequests() // Refresh the list
        }
    }
}

