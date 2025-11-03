package com.qaassist.inspection.viewmodels

import android.app.Application
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.qaassist.inspection.database.entities.InspectionEntity
import com.qaassist.inspection.models.InspectionForm
import com.qaassist.inspection.models.PhotoItem
import com.qaassist.inspection.repository.InspectionRepository
import com.qaassist.inspection.utils.ExcelGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class InspectionViewModel(application: Application) : AndroidViewModel(application) {

    private val _photos = MutableLiveData<List<PhotoItem>>(emptyList())
    val photos: LiveData<List<PhotoItem>> = _photos

    private val _saveStatus = MutableLiveData<Boolean>()
    val saveStatus: LiveData<Boolean> = _saveStatus

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    
    private val _loadedInspection = MutableLiveData<InspectionEntity?>()
    val loadedInspection: LiveData<InspectionEntity?> = _loadedInspection

    private val _navigateToInspectionTab = MutableLiveData<Boolean>()
    val navigateToInspectionTab: LiveData<Boolean> = _navigateToInspectionTab

    val showCustomProjectField = MutableLiveData<Boolean>()
    val showCustomMunicipalityField = MutableLiveData<Boolean>()

    val municipalityMap = mapOf(
        "5080562" to listOf("Allisonville", "Bath", "Belleville", "Picton", "Quinte", "Trenton"),
        "5080569" to listOf("PR"),
        "5080570" to listOf("SDG"),
        "5080572" to listOf("Lanark", "McDonalds Corners"),
        "5080585" to listOf("Dessoronto", "Stirling", "Thurlow", "Napanee")
    )

    private val inspectionRepository = InspectionRepository(application)
    private val ROOT_DIRECTORY = "QA-Assist"
    private val PROJECTS_TO_HIDE_OLT_FSA = listOf("5080572", "5080585")
    
    fun loadInspectionForEdit(inspection: InspectionEntity) {
        _loadedInspection.value = inspection
        
        val photoUris = parsePhotoPaths(inspection.photosPaths)
        val photoItems = photoUris.map { uriString ->
            PhotoItem(
                path = uriString,
                uri = uriString.toUri(),
                timestamp = System.currentTimeMillis()
            )
        }
        _photos.value = photoItems
        _navigateToInspectionTab.value = true
    }
    
    fun onInspectionLoaded() {
        _loadedInspection.value = null
    }

    fun onNavigationHandled() {
        _navigateToInspectionTab.value = false
    }

    private fun parsePhotoPaths(json: String): List<String> {
        if (json.isBlank()) return emptyList()
        return try {
            val type = object : TypeToken<List<String>>() {}.type
            Gson().fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun addPhotoFromUri(uri: Uri) {
        val currentPhotos = _photos.value ?: emptyList()
        val newPhoto = PhotoItem(path = uri.toString(), uri = uri, timestamp = System.currentTimeMillis())
        _photos.value = currentPhotos + newPhoto
    }

    fun updatePhotos(photos: List<PhotoItem>) {
        _photos.value = photos
    }

    fun clearPhotos() {
        _photos.value = emptyList()
    }
    
    private fun getPathFromUri(uri: Uri): String? {
        val context = getApplication<Application>().applicationContext
        var path: String? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                 context.contentResolver.query(uri, arrayOf(MediaStore.MediaColumns.DATA), null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                        path = cursor.getString(columnIndex)
                    }
                }
            } catch(e: Exception) {
                Log.w("InspectionViewModel", "Failed to get path from MediaStore for URI: $uri", e)
            }
        }
        if (path == null) {
            path = uri.path
        }
        return path
    }

    fun saveInspection(form: InspectionForm, status: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val context = getApplication<Application>().applicationContext
            try {
                val getSafeString = { input: String? -> input?.replace(Regex("[/\\\\:*?\"<>|]"), "") ?: "N_A" }
                val safeProject = getSafeString(form.project)
                val safeMunicipality = getSafeString(form.municipality)
                val safeOlt = getSafeString(form.olt)
                val safeFsa = getSafeString(form.fsa)
                val safeAsBuilt = getSafeString(form.asBuilt)
                val safeInspectionType = getSafeString(form.inspectionType)
                val safeEquipmentId = getSafeString(form.equipmentId)
                val safeAddress = getSafeString(form.address)
                val safeDrawing = getSafeString(form.drawing)

                val baseFileName = "$safeEquipmentId-$safeDrawing-$safeAddress"
                val excelDisplayName = "$baseFileName.xlsx"
                
                val statusDir = when (status) {
                    "Deficiencies Present" -> "QA-Inspections With Deficiencies"
                    "Ready For Final" -> "QA-Inspections Ready For Final"
                    else -> "QA-Inspections"
                }
                
                val projectSubDir = if (form.project in PROJECTS_TO_HIDE_OLT_FSA) {
                    "$safeProject/$safeMunicipality/$safeAsBuilt/$safeInspectionType/$baseFileName"
                } else {
                    "$safeProject/$safeMunicipality/OLT-$safeOlt/FSA-$safeFsa/$safeAsBuilt/$safeInspectionType/$baseFileName"
                }
                val subdirectory = "$statusDir/$projectSubDir"

                val resolver = context.contentResolver
                val finalCopiedPhotoUris = mutableListOf<String>()
                var finalExcelUri: Uri? = null
                var finalExcelPath: String? = null

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val relativePath = "${Environment.DIRECTORY_DOCUMENTS}/$ROOT_DIRECTORY/$subdirectory"
                    val collection = MediaStore.Files.getContentUri("external")

                    form.photos.forEachIndexed { index, photoItem ->
                        val photoDisplayName = "${baseFileName}_photo_${index + 1}.jpg"
                        val photoValues = ContentValues().apply {
                            put(MediaStore.MediaColumns.DISPLAY_NAME, photoDisplayName)
                            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                            put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
                        }
                        val newPhotoUri = resolver.insert(collection, photoValues)
                        if (newPhotoUri != null) {
                            resolver.openInputStream(photoItem.uri)?.use { input ->
                                resolver.openOutputStream(newPhotoUri)?.use { output ->
                                    input.copyTo(output)
                                }
                            }
                            finalCopiedPhotoUris.add(newPhotoUri.toString())
                        }
                    }

                    val excelValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, excelDisplayName)
                        put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
                    }
                    finalExcelUri = resolver.insert(collection, excelValues)
                    finalExcelPath = getPathFromUri(finalExcelUri!!)

                } else {
                    val documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                    val finalDir = File(documentsDir, "$ROOT_DIRECTORY/$subdirectory")
                    finalDir.mkdirs()

                    form.photos.forEachIndexed { index, photoItem ->
                        val photoDisplayName = "${baseFileName}_photo_${index + 1}.jpg"
                        val photoFile = File(finalDir, photoDisplayName)
                        resolver.openInputStream(photoItem.uri)?.use { input ->
                            FileOutputStream(photoFile).use { output ->
                                input.copyTo(output)
                            }
                        }
                        finalCopiedPhotoUris.add(Uri.fromFile(photoFile).toString())
                    }

                    val excelFile = File(finalDir, excelDisplayName)
                    finalExcelUri = Uri.fromFile(excelFile)
                    finalExcelPath = excelFile.absolutePath
                }

                if (finalExcelUri != null) {
                    ExcelGenerator.generateExcel(context, form, finalExcelUri)
                    
                    val finalForm = form.copy(
                        excelUri = finalExcelUri.toString(),
                        excelPath = finalExcelPath ?: "",
                        photos = finalCopiedPhotoUris.map { PhotoItem(path = it, uri = it.toUri(), timestamp = System.currentTimeMillis()) }
                    )
                    inspectionRepository.saveInspection(finalForm)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Inspection saved successfully", Toast.LENGTH_LONG).show()
                        _saveStatus.value = true
                    }
                } else {
                    throw Exception("Failed to create Excel file URI.")
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, e.message ?: "An unknown error occurred", Toast.LENGTH_LONG).show()
                    _errorMessage.value = e.message
                }
            } finally {
                _isLoading.value = false
            }
        }
    }
}