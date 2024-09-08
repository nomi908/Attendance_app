package com.example.attendanceapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.attendanceapp.data.dao.AdminDao
import com.example.attendanceapp.data.dao.AttendanceDao
import com.example.attendanceapp.data.dao.LeaveDao
import com.example.attendanceapp.data.dao.UserDao
import com.example.attendanceapp.data.entities.Admin
import com.example.attendanceapp.data.entities.Attendance
import com.example.attendanceapp.data.entities.Leave
import com.example.attendanceapp.data.entities.User


@Database(entities = [User::class, Attendance::class, Leave::class, Admin::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun leaveDao(): LeaveDao
    abstract fun AdminDao() : AdminDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "attendance_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}