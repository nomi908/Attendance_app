package com.example.attendanceapp.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.attendanceapp.data.entities.Attendance
import com.example.attendanceapp.data.entities.User

@Dao
interface AttendanceDao {
    @Insert
    suspend fun insert(attendance: Attendance)


    @Update
    suspend fun update(attendance: Attendance)
    @Query("UPDATE attendance SET status = :status, date = :date WHERE userId = :userId")
    suspend fun updateAttendance(userId: Int, status: String, date: String)

    @Query("DELETE FROM attendance WHERE userId = :userId")

    suspend fun delete(userId: Int)
    @Query("SELECT * FROM attendance")
    suspend fun getAllAttendances(): List<Attendance>


    @Query("SELECT * FROM attendance WHERE userId = :userId AND date LIKE :datePrefix || '%'")
    suspend fun getAttendanceByUserId(userId: Int, datePrefix: String): List<Attendance>

    @Query("SELECT * FROM attendance")
    suspend fun getAllUsers(): List<Attendance>

    @Query("SELECT * FROM attendance WHERE userId = :userId")
    suspend fun getAttendanceByUserId(userId: Int): List<Attendance>

    @Query("SELECT * FROM attendance WHERE userId = :userId AND date BETWEEN :startDate AND :endDate")
    suspend fun searchAttendancesByDateRangeAndUserId(startDate: String, endDate: String, userId: Int): List<Attendance>



}