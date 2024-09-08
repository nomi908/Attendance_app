//package com.example.attendanceapp.ui.admin
//
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import androidx.lifecycle.ReportFragment.Companion.reportFragment
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.example.attendanceapp.R
//import com.example.attendanceapp.data.AppDatabase
//import com.example.attendanceapp.data.entities.Attendance
//import com.example.attendanceapp.data.entities.User
//import com.example.attendanceapp.ui.Adapter.UserAdapter
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//
//class All_Studenlist_Activity : AppCompatActivity() {
//
//    private lateinit var db: AppDatabase
//    private  lateinit var stdList : RecyclerView
//    override fun onCreate(savedInstanceState: Bundle?) {
//
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_all_studenlist)
//        db = AppDatabase.getDatabase(this)
//        stdList = findViewById(R.id.studentRcv)
//
//
//
//
//
//    }
//
//
//    override fun onResume() {
//        super.onResume()
//        CoroutineScope(Dispatchers.IO).launch {
//            val users = withContext(Dispatchers.IO) {
//                db.userDao().getAllUsers()
//            }
//            val attendanceList = withContext(Dispatchers.IO){
//                db.attendanceDao().getAllUsers()
//            }
//            withContext(Dispatchers.Main){
//                stdList.layoutManager = LinearLayoutManager(this@All_Studenlist_Activity, LinearLayoutManager.VERTICAL, false)
//                stdList.adapter = users?.let { UserAdapter(users as ArrayList<User>, attendanceList as ArrayList<Attendance>) }
//
//            }
//
//
//
//
//        }
//    }
//
//}

package com.example.attendanceapp.ui.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.attendanceapp.R
import com.example.attendanceapp.data.AppDatabase
import com.example.attendanceapp.data.entities.Attendance
import com.example.attendanceapp.data.entities.User
import com.example.attendanceapp.ui.Adapter.UserAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class All_Studenlist_Activity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var stdList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_studenlist)
        db = AppDatabase.getDatabase(this)
        stdList = findViewById(R.id.studentRcv)
    }

    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.IO).launch {
            val users = db.userDao().getAllUsers().toCollection(ArrayList())
            val attendanceList = db.attendanceDao().getAllAttendances().toCollection(ArrayList())// Ensure this method exists

            withContext(Dispatchers.Main) {
                stdList.layoutManager = LinearLayoutManager(this@All_Studenlist_Activity, LinearLayoutManager.VERTICAL, false)
                stdList.adapter = UserAdapter(users, attendanceList,) // Pass lists without casting
            }
        }
    }


}
