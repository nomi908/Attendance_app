package com.example.attendanceapp.data.entities

import android.hardware.camera2.CameraExtensionSession.StillCaptureLatency
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "leaves")
data class Leave(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val toDate: String,
    val fromDate:String,
    val levreason : String,
    val subject : String,
    val status: String // e.g., "Approved", "Pending"
) : Serializable