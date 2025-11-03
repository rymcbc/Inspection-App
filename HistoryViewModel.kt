package com.qaassist.inspection.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.qaassist.inspection.database.entities.InspectionEntity
import com.qaassist.inspection.repository.InspectionRepository
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = InspectionRepository(application)
    
    private val _inspections = MutableLiveData<List<InspectionEntity>>()
    val inspections: LiveData<List<InspectionEntity>> = _inspections
    
    private val _filteredInspections = MutableLiveData<List<InspectionEntity>>()
    val filteredInspections: LiveData<List<InspectionEntity>> = _filteredInspections
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: LiveData<String> = _searchQuery
    
    private var allInspections: List<InspectionEntity> = emptyList()
    private var currentSortField: String? = null
    private var currentSortAscending: Boolean = true
    
    init {
        loadInspections()
    }
    
    fun loadInspections() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                allInspections = repository.getAllInspections()
                _inspections.value = allInspections
                _filteredInspections.value = allInspections
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun searchInspections(query: String) {
        _searchQuery.value = query
        val filtered = if (query.isEmpty()) {
            allInspections
        } else {
            allInspections.filter { inspection ->
                inspection.project.contains(query, ignoreCase = true) ||
                inspection.olt.contains(query, ignoreCase = true) ||
                inspection.fsa.contains(query, ignoreCase = true) ||
                inspection.asBuilt.contains(query, ignoreCase = true) ||
                inspection.inspectionType.contains(query, ignoreCase = true) ||
                inspection.equipmentId.contains(query, ignoreCase = true) ||
                inspection.address.contains(query, ignoreCase = true) ||
                inspection.drawing.contains(query, ignoreCase = true)
            }
        }
        _filteredInspections.value = filtered
    }
    
    fun filterByField(fieldName: String, value: String) {
        val filtered = if (value.isEmpty()) {
            allInspections
        } else {
            allInspections.filter { inspection ->
                when (fieldName) {
                    "project" -> inspection.project == value
                    "olt" -> inspection.olt == value
                    "fsa" -> inspection.fsa == value
                    "asBuilt" -> inspection.asBuilt == value
                    "inspectionType" -> inspection.inspectionType == value
                    "equipmentId" -> inspection.equipmentId == value
                    "address" -> inspection.address == value
                    "drawing" -> inspection.drawing == value
                    else -> true
                }
            }
        }
        _filteredInspections.value = filtered
    }
    
    fun sortBy(fieldName: String) {
        val ascending = if (currentSortField == fieldName) !currentSortAscending else true
        currentSortField = fieldName
        currentSortAscending = ascending
        
        val sorted = (_filteredInspections.value ?: emptyList()).sortedWith { a, b ->
            val comparison = when (fieldName) {
                "date" -> a.date.compareTo(b.date)
                "project" -> a.project.compareTo(b.project)
                "olt" -> a.olt.compareTo(b.olt)
                "fsa" -> a.fsa.compareTo(b.fsa)
                "asBuilt" -> a.asBuilt.compareTo(b.asBuilt)
                "inspectionType" -> a.inspectionType.compareTo(b.inspectionType)
                "equipmentId" -> a.equipmentId.compareTo(b.equipmentId)
                "address" -> a.address.compareTo(b.address)
                "drawing" -> a.drawing.compareTo(b.drawing)
                else -> 0
            }
            if (ascending) comparison else -comparison
        }
        
        _filteredInspections.value = sorted
    }
}