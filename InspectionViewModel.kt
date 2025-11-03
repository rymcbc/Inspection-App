package com.qaassist.inspection.viewmodels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.qaassist.inspection.models.InspectionForm
import com.qaassist.inspection.models.PhotoItem
import com.qaassist.inspection.repository.InspectionRepository
import com.qaassist.inspection.utils.FileUtils
import com.qaassist.inspection.utils.LocationUtils
import com.qaassist.inspection.utils.PDFGenerator
import kotlinx.coroutines.launch
import java.io.File

class InspectionViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = InspectionRepository(application)
    private val locationUtils = LocationUtils(application)
    private val pdfGenerator = PDFGenerator(application)
    private val fileUtils = FileUtils(application)
    
    private val _photos = MutableLiveData<List<PhotoItem>>()
    val photos: LiveData<List<PhotoItem>> = _photos
    
    private val _saveStatus = MutableLiveData<Boolean>()
    val saveStatus: LiveData<Boolean> = _saveStatus
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage
    
    private val photoList = mutableListOf<PhotoItem>()
    
    init {
        _photos.value = emptyList()
    }
    
    fun addPhoto(photoPath: String) {
        val photoItem = PhotoItem(
            path = photoPath,
            uri = Uri.fromFile(File(photoPath)),
            timestamp = System.currentTimeMillis()
        )
        photoList.add(photoItem)
        _photos.value = photoList.toList()
    }
    
    fun addPhotoFromUri(uri: Uri) {
        viewModelScope.launch {
            try {
                val photoPath = fileUtils.copyUriToInternalStorage(uri)
                val photoItem = PhotoItem(
                    path = photoPath,
                    uri = uri,
                    timestamp = System.currentTimeMillis()
                )
                photoList.add(photoItem)
                _photos.value = photoList.toList()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to add photo: ${e.message}"
            }
        }
    }
    
    fun removePhoto(position: Int) {
        if (position >= 0 && position < photoList.size) {
            photoList.removeAt(position)
            _photos.value = photoList.toList()
        }
    }
    
    fun clearPhotos() {
        photoList.clear()
        _photos.value = emptyList()
    }
    
    suspend fun saveInspection(form: InspectionForm) {
        _isLoading.value = true
        
        try {
            // Get current location
            val location = locationUtils.getCurrentLocation()
            val formWithLocation = form.copy(
                latitude = location?.latitude,
                longitude = location?.longitude,
                photos = photoList.toList()
            )
            
            // Create directory structure
            val targetDirectory = fileUtils.createDirectoryStructure(formWithLocation)
            
            // Generate file name from lines 6, 8, 7
            val fileName = fileUtils.generateFileName(
                formWithLocation.equipmentId,
                formWithLocation.address,
                formWithLocation.drawing
            )
            
            // Copy photos to target directory
            val copiedPhotos = fileUtils.copyPhotosToDirectory(photoList, targetDirectory, fileName)
            
            // Generate PDF
            val pdfFile = pdfGenerator.generatePDF(
                formWithLocation.copy(photos = copiedPhotos),
                targetDirectory,
                fileName
            )
            
            // Save to database
            repository.saveInspection(formWithLocation.copy(pdfPath = pdfFile.absolutePath))
            
            _saveStatus.value = true
        } catch (e: Exception) {
            _errorMessage.value = "Failed to save inspection: ${e.message}"
            _saveStatus.value = false
        } finally {
            _isLoading.value = false
        }
    }
}