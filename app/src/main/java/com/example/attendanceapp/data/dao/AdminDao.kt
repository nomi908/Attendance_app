package com.example.attendanceapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.attendanceapp.data.entities.Admin

@Dao
interface AdminDao {

    @Insert
    suspend fun insert(admin: Admin)

    @Update
    suspend fun update(admin: Admin)

    @Query("DELETE FROM admin WHERE id = :adminId")
    suspend fun deleteById(adminId: Int)

    @Query("SELECT * FROM admin WHERE id = :adminId")
    suspend fun getAdminById(adminId: Int): Admin?

    @Query("SELECT * FROM admin WHERE email = :email AND password = :password LIMIT 1")
    suspend fun getAdminByEmailAndPassword(email: String, password: String): Admin?

    @Query("UPDATE admin SET profilePicture = :profilePicture WHERE id = :adminId")
    suspend fun updateProfilePicture(adminId: Int, profilePicture: String)

    @Query("SELECT * FROM admin")
    suspend fun getAllAdmins(): List<Admin>
}