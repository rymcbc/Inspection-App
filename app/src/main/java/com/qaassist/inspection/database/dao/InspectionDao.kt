package com.qaassist.inspection.database.dao

import androidx.room.*
import com.qaassist.inspection.database.entities.InspectionEntity

@Dao
interface InspectionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInspection(inspection: InspectionEntity): Long

    @Update
    suspend fun updateInspection(inspection: InspectionEntity)

    @Delete
    suspend fun deleteInspection(inspection: InspectionEntity)

    @Query("DELETE FROM inspections WHERE id IN (:inspectionIds)")
    suspend fun deleteInspections(inspectionIds: Set<Long>)

    @Query("SELECT * FROM inspections ORDER BY id DESC")
    suspend fun getAllInspections(): List<InspectionEntity>

    @Query("SELECT * FROM inspections WHERE id = :id")
    suspend fun getInspectionById(id: Long): InspectionEntity?

    @Query("SELECT * FROM inspections WHERE project = :project")
    suspend fun getInspectionsByProject(project: String): List<InspectionEntity>

    @Query("SELECT * FROM inspections WHERE inspectionType = :type")
    suspend fun getInspectionsByType(type: String): List<InspectionEntity>

    @Query("SELECT COUNT(*) FROM inspections")
    suspend fun getInspectionCount(): Int
}

