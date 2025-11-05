package com.qaassist.inspection.repository

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.qaassist.inspection.database.AppDatabase
import com.qaassist.inspection.database.entities.InspectionEntity
import com.qaassist.inspection.models.InspectionForm
import java.io.File

class InspectionRepository(private val context: Context) {
    
    private val database = AppDatabase.getDatabase(context)
    private val inspectionDao = database.inspectionDao()
    private val gson = Gson()
    private val tag = "InspectionRepo_DEBUG"
    
    suspend fun saveInspection(form: InspectionForm): Long {
        return try {
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
                excelPath = form.excelPath ?: "",
                excelUri = form.excelUri ?: "",
                photosPaths = photoPathsJson
            )
            
            val id = inspectionDao.insertInspection(entity)
            Log.i("InspectionRepository", "Saved inspection with ID: $id")
            id
        } catch (e: Exception) {
            Log.e("InspectionRepository", "Error saving inspection", e)
            throw e
        }
    }
    
    suspend fun deleteInspectionWithFiles(inspection: InspectionEntity) {
        Log.d(tag, "--- Starting Deletion Process ---")
        Log.d(tag, "Attempting to delete inspection: $inspection")
        try {
            // 1. Delete all photo files
            if (inspection.photosPaths.isNotBlank()) {
                val photoPaths = parsePhotoPaths(inspection.photosPaths)
                Log.d(tag, "Found ${photoPaths.size} photo paths to delete.")
                for (path in photoPaths) {
                    deleteFileFromUriString(path)
                }
            } else {
                Log.d(tag, "No photo paths found in this inspection record.")
            }

            // 2. Delete the Excel file
            if (inspection.excelUri.isNotBlank()) {
                Log.d(tag, "Attempting to delete Excel file.")
                deleteFileFromUriString(inspection.excelUri)
            } else {
                Log.d(tag, "No Excel URI found in this inspection record.")
            }

            // 3. Recursively delete the parent directory
            if (inspection.excelPath.isNotBlank()) {
                try {
                    val excelFile = File(inspection.excelPath)
                    val parentDir = excelFile.parentFile
                    if (parentDir != null && parentDir.exists() && parentDir.isDirectory) {
                        Log.d(tag, "Attempting to recursively delete parent directory: ${parentDir.absolutePath}")
                        val success = parentDir.deleteRecursively()
                        Log.d(tag, "Result of recursive delete for '${parentDir.absolutePath}': $success")
                    } else {
                        Log.w(tag, "Parent directory not found or not a directory for path: ${inspection.excelPath}")
                    }
                } catch (e: Exception) {
                    Log.e(tag, "Error recursively deleting parent directory for path: ${inspection.excelPath}", e)
                }
            } else {
                 Log.w(tag, "excelPath is blank, cannot determine parent directory for deletion.")
            }

            // 4. Finally, delete the inspection from the database
            Log.d(tag, "Attempting to delete inspection record from database (ID: ${inspection.id}).")
            inspectionDao.deleteInspection(inspection)
            Log.d(tag, "Successfully deleted inspection record from database.")

        } catch (e: Exception) {
            Log.e(tag, "A critical error occurred in deleteInspectionWithFiles", e)
            throw e
        }
        Log.d(tag, "--- Finished Deletion Process ---")
    }

    private fun deleteFileFromUriString(uriString: String) {
        Log.d(tag, "deleteFileFromUriString: Processing URI string: '$uriString'")
        if (uriString.isBlank()) {
            Log.w(tag, "deleteFileFromUriString: Received a blank URI string. Skipping.")
            return
        }
        try {
            val uri = uriString.toUri()
            Log.d(tag, "deleteFileFromUriString: Parsed URI. Scheme: '${uri.scheme}'")
            if (uri.scheme == "content") {
                val deletedRows = context.contentResolver.delete(uri, null, null)
                Log.d(tag, "deleteFileFromUriString: Attempted to delete content URI. Rows affected: $deletedRows")
            } else if (uri.scheme == "file") {
                val path = uri.path
                if(path != null) {
                    val file = File(path)
                    val result = file.delete()
                    Log.d(tag, "deleteFileFromUriString: Attempted to delete file URI. Path: '${file.absolutePath}'. Success: $result")
                } else {
                    Log.w(tag, "deleteFileFromUriString: File URI has a null path. Cannot delete.")
                }
            } else {
                Log.w(tag, "deleteFileFromUriString: Unhandled URI scheme: '${uri.scheme}'")
            }
        } catch (e: Exception) {
            Log.e(tag, "deleteFileFromUriString: Error processing URI string: '$uriString'", e)
        }
    }
    
    suspend fun getAllInspections(): List<InspectionEntity> {
        return try {
            inspectionDao.getAllInspections()
        } catch (e: Exception) {
            Log.e("InspectionRepository", "Error loading inspections", e)
            throw e
        }
    }
    
    suspend fun getInspectionById(id: Long): InspectionEntity? {
        return try {
            inspectionDao.getInspectionById(id)
        } catch (e: Exception) {
            Log.e("InspectionRepository", "Error loading inspection by ID: $id", e)
            null
        }
    }
    
    suspend fun updateInspection(entity: InspectionEntity) {
        try {
            inspectionDao.updateInspection(entity)
        } catch (e: Exception) {
            Log.e("InspectionRepository", "Error updating inspection", e)
            throw e
        }
    }
    
    suspend fun deleteInspection(entity: InspectionEntity) {
        try {
            inspectionDao.deleteInspection(entity)
        } catch (e: Exception) {
            Log.e("InspectionRepository", "Error deleting inspection", e)
            throw e
        }
    }
    
    suspend fun deleteInspections(inspectionIds: Set<Long>) {
        try {
            inspectionDao.deleteInspections(inspectionIds)
        } catch (e: Exception) {
            Log.e("InspectionRepository", "Error deleting inspections", e)
            throw e
        }
    }
    
    suspend fun getInspectionsByProject(project: String): List<InspectionEntity> {
        return try {
            inspectionDao.getInspectionsByProject(project)
        } catch (e: Exception) {
            Log.e("InspectionRepository", "Error loading inspections by project: $project", e)
            emptyList<InspectionEntity>()
        }
    }
    
    suspend fun getInspectionsByType(type: String): List<InspectionEntity> {
        return try {
            inspectionDao.getInspectionsByType(type)
        } catch (e: Exception) {
            Log.e("InspectionRepository", "Error loading inspections by type: $type", e)
            emptyList<InspectionEntity>()
        }
    }
    
    suspend fun getInspectionCount(): Int {
        return try {
            inspectionDao.getInspectionCount()
        } catch (e: Exception) {
            Log.e("InspectionRepository", "Error getting inspection count", e)
            0
        }
    }

    suspend fun getInspections(): List<InspectionEntity> {
        return try {
            inspectionDao.getAllInspections()
        } catch (e: Exception) {
            Log.e("InspectionRepository", "Error loading inspections", e)
            emptyList<InspectionEntity>()
        }
    }

    private fun parsePhotoPaths(json: String): List<String> {
        return try {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(json, type) ?: emptyList<String>()
        } catch (e: Exception) {
            Log.e("InspectionRepository", "Error parsing photo paths JSON", e)
            emptyList<String>()
        }
    }
}