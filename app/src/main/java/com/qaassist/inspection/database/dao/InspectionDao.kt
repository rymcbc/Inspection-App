package com.qaassist.inspection.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.qaassist.inspection.database.entities.InspectionEntity

@Dao
interface InspectionDao {
    
    @Query("SELECT * FROM inspections ORDER BY createdTimestamp DESC")
    suspend fun getAllInspections(): List<InspectionEntity>
    
    @Query("SELECT * FROM inspections WHERE id = :id")
    suspend fun getInspectionById(id: Long): InspectionEntity?
    
    @Insert
    suspend fun insertInspection(inspection: InspectionEntity): Long
    
    @Update
    suspend fun updateInspection(inspection: InspectionEntity)
    
    @Delete
    suspend fun deleteInspection(inspection: InspectionEntity)
    
    @Query("DELETE FROM inspections WHERE id IN (:inspectionIds)")
    suspend fun deleteInspections(inspectionIds: Set<Long>)
    
    @Query("SELECT * FROM inspections WHERE project = :project ORDER BY createdTimestamp DESC")
    suspend fun getInspectionsByProject(project: String): List<InspectionEntity>
    
    @Query("SELECT * FROM inspections WHERE inspectionType = :type ORDER BY createdTimestamp DESC")
    suspend fun getInspectionsByType(type: String): List<InspectionEntity>
    
    @Query("SELECT DISTINCT project FROM inspections ORDER BY project")
    suspend fun getAllProjects(): List<String>
    
    @Query("SELECT DISTINCT inspectionType FROM inspections ORDER BY inspectionType")
    suspend fun getAllInspectionTypes(): List<String>
    
    @Query("SELECT COUNT(*) FROM inspections")
    suspend fun getInspectionCount(): Int
}