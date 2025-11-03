package com.qaassist.inspection.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inspections")
data class InspectionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String,
    val project: String,
    val olt: String,
    val fsa: String,
    val asBuilt: String,
    val inspectionType: String,
    val equipmentId: String,
    val address: String,
    val drawing: String,
    val observations: String,
    val latitude: Double?,
    val longitude: Double?,
    val pdfPath: String,
    val photosPaths: String, // JSON string of photo paths
    val createdTimestamp: Long = System.currentTimeMillis()
)