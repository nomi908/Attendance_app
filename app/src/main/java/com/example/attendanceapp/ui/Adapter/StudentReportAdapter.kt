package com.example.attendanceapp.ui.Adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.attendanceapp.R
import com.example.attendanceapp.data.entities.Attendance
import com.example.attendanceapp.data.entities.User
import com.example.attendanceapp.ui.admin.StudentRecordSearch
import com.example.attendanceapp.ui.admin.UserDetailsActivity

class StudentReportAdapter(val users: ArrayList<User>, val attendanceList: ArrayList<Attendance>,
) : RecyclerView.Adapter<StudentReportAdapter.viewHolder>() {
    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userImg : ImageView = itemView.findViewById(R.id.userImg)
        val stdEmail : TextView = itemView.findViewById(R.id.stdEmail)
        val stdName : TextView = itemView.findViewById(R.id.stdName)
        val chkinDate : TextView = itemView.findViewById(R.id.chkinDate)
        val attdStatus : TextView = itemView.findViewById(R.id.attdStatus)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentReportAdapter.viewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_userlist, parent, false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val user = users[position]
        holder.stdName.text = user.username
        holder.stdEmail.text = user.email



        Glide.with(holder.itemView.context)
            .load(user.profilePicture)
            .placeholder(R.drawable.ic_user)
            .into(holder.userImg)
        // Debug logs
        Log.d("UserAdapter", "Binding user: $user")
        Log.d("UserAdapter", "Searching for attendance with userId: ${user.id}")

        val attendance = attendanceList.find { it.userId == user.id }

        if (attendance == null) {
            Log.d("UserAdapter", "No attendance found for userId: ${user.id}")
        } else {
            Log.d("UserAdapter", "Found attendance: $attendance")
        }

        holder.attdStatus.text = attendance?.status ?: "Absent"
        holder.chkinDate.text = attendance?.date ?: "N/A"

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, StudentRecordSearch::class.java).apply {
                putExtra("username", user.username)
                putExtra("useremail", user.email)
                putExtra("profilepicture", user.profilePicture)
                putExtra("attdstatus", attendance?.status)
                putExtra("chkindate", attendance?.date)
                putExtra("userid", user.id) // Pass user ID
            }
            Log.d("UserAdapter", "Starting UserDetailsActivity with intent: $intent")
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return users.size
    }
}