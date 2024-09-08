package com.example.attendanceapp.ui.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.attendanceapp.R
import com.example.attendanceapp.data.AppDatabase
import com.example.attendanceapp.data.dao.UserDao
import com.example.attendanceapp.data.entities.Leave
import com.example.attendanceapp.data.entities.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LeaveRequestAdapter(
    private var leaveRequests: List<Leave>,
    private val onApproveClick: (Leave) -> Unit,
    private val onRejectClick: (Leave) -> Unit,
    private val userDao: UserDao
) : RecyclerView.Adapter<LeaveRequestAdapter.LeaveRequestViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaveRequestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leavelist, parent, false)
        return LeaveRequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: LeaveRequestViewHolder, position: Int) {
        val leave = leaveRequests[position]
        Log.d("UserFetch", "Fetching user with ID: ${leave.userId}")

        CoroutineScope(Dispatchers.IO).launch {
            val user = userDao.getUserById(leave.id)
            withContext(Dispatchers.Main){
                holder.userName.text = user?.username // Fetch actual user name if available
                Log.d("uname", "onBindViewHolder: "+ user?.username)
                holder.userEmail.text = user?.email // Fetch actual user email if available
                Log.d("uemail", "onBindViewHolder: "+ user?.email)


            }
        }

        holder.leaveSubject.text = leave.subject
        holder.leaveDescription.text = leave.levreason
        holder.leaveDates.text = "From: ${leave.fromDate} - To: ${leave.toDate}"

        holder.btnApprove.setOnClickListener {
            onApproveClick(leave)
        }

        holder.btnReject.setOnClickListener {
            onRejectClick(leave)
        }
    }

    override fun getItemCount() = leaveRequests.size

    fun updateLeaveRequests(newLeaveRequests: List<Leave>) {
        leaveRequests = newLeaveRequests
        notifyDataSetChanged()
    }

    inner class LeaveRequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.userName)
        val userEmail: TextView = itemView.findViewById(R.id.userEmail)
        val leaveSubject: TextView = itemView.findViewById(R.id.leaveSubject)
        val leaveDescription: TextView = itemView.findViewById(R.id.leaveDescription)
        val leaveDates: TextView = itemView.findViewById(R.id.leaveDates)
        val btnApprove: Button = itemView.findViewById(R.id.btnApprove)
        val btnReject: Button = itemView.findViewById(R.id.btnReject)
    }
}
