package com.qaassist.inspection.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.qaassist.inspection.database.AppDatabase
import com.qaassist.inspection.database.entities.InspectionEntity
import com.qaassist.inspection.models.InspectionForm
import com.qaassist.inspection.models.PhotoItem

class InspectionRepository(context: Context) {
    
    private val database = AppDatabase.getDatabase(context)
    private val inspectionDao = database.inspectionDao()
    private val gson = Gson()
    
    suspend fun saveInspection(form: InspectionForm): Long {
        val photoPaths = form.photos.map { it.path }
        val photoPathsJson = gson.toJson(photoPaths)
        
        val entity = InspectionEntity(
            date = form.date,
            project = form.project,
            olt = form.olt,
            fsa = form.fsa,
            asBuilt = form.asBuilt,
            inspectionType = form.inspectionType,
            equipmentId = form.equipmentId,
            address = form.address,
            drawing = form.drawing,
            observations = form.observations,
            latitude = form.latitude,
            longitude = form.longitude,
            pdfPath = form.pdfPath ?: "",
            photosPaths = photoPathsJson
        )
        
        return inspectionDao.insertInspection(entity)
    }
    
    suspend fun getAllInspections(): List<InspectionEntity> {
        return inspectionDao.getAllInspections()
    }
    
    suspend fun getInspectionById(id: Long): InspectionEntity? {
        return inspectionDao.getInspectionById(id)
    }
    
    suspend fun updateInspection(entity: InspectionEntity) {
        inspectionDao.updateInspection(entity)
    }
    
    suspend fun deleteInspection(entity: InspectionEntity) {
        inspectionDao.deleteInspection(entity)
    }
    
    suspend fun getInspectionsByProject(project: String): List<InspectionEntity> {
        return inspectionDao.getInspectionsByProject(project)
    }
    
    suspend fun getInspectionsByType(type: String): List<InspectionEntity> {
        return inspectionDao.getInspectionsByType(type)
    }
    
    private fun parsePhotoPaths(json: String): List<String> {
        return try {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}