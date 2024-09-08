package com.example.attendanceapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.attendanceapp.data.entities.Leave

@Dao
interface LeaveDao {
    @Insert
    suspend fun insert(leave: Leave)

    @Query("SELECT * FROM leaves")
    suspend fun getAllLeaves(): List<Leave>

    @Query("UPDATE leaves SET status = :status WHERE id = :id")
    suspend fun updateLeaveStatus(id: Int, status: String)

    @Query("SELECT * FROM leaves WHERE userId = :userId")
    suspend fun getLeavesByUserId(userId: Int): Leave?
}