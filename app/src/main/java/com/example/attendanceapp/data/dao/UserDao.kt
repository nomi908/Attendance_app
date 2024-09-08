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
interface UserDao {
    @Insert
    suspend fun insert(user: User)


    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun getUserByEmailOrUsernameAndPassword(email: String, password: String): User?

    @Query("UPDATE users SET profilePicture = :profilePicture WHERE id = :userId")
    suspend fun updateProfilePicture(userId: Int, profilePicture: String)

    @Query("SELECT * FROM users WHERE id =:userId")
    suspend fun getUserById(userId: Int): User?

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    @Query("SELECT * FROM users WHERE username LIKE :username")
    fun searchUsers(username: String): List<User>

    @Query("SELECT * FROM users WHERE email =:emaily")
    suspend fun getUserByEmail(emaily: String): List<User>

}