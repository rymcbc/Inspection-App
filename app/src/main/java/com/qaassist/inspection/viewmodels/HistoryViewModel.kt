package com.qaassist.inspection.viewmodels

import android.app.Application
import android.util.Log
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

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    // LiveData for individual filters
    private val _searchQuery = MutableLiveData<String>("")
    private val _filterProject = MutableLiveData<String>("All")
    private val _filterInspectionType = MutableLiveData<String>("All")
    private val _filterDate = MutableLiveData<String>("")
    private val _filterOlt = MutableLiveData<String>("All")
    private val _filterFsa = MutableLiveData<String>("All")
    private val _filterAsBuilt = MutableLiveData<String>("All")

    val currentFilters = mutableMapOf<String, String>()

    private var allInspections: List<InspectionEntity> = emptyList()
    private var currentSortField: String? = null
    private var currentSortAscending: Boolean = true

    init {
        // Initialize currentFilters with default values that match LiveData
        currentFilters["search"] = _searchQuery.value ?: ""
        currentFilters["project"] = _filterProject.value ?: "All"
        currentFilters["inspectionType"] = _filterInspectionType.value ?: "All"
        currentFilters["date"] = _filterDate.value ?: ""
        currentFilters["olt"] = _filterOlt.value ?: "All"
        currentFilters["fsa"] = _filterFsa.value ?: "All"
        currentFilters["asBuilt"] = _filterAsBuilt.value ?: "All"
        loadInspections()
    }

    private fun loadInspections() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                allInspections = repository.getAllInspections()
                _inspections.value = allInspections
                applyFilters() // Apply current filters to the newly loaded data
                Log.i("HistoryViewModel", "Loaded ${allInspections.size} inspections")
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load inspections: ${e.message}"
                Log.e("HistoryViewModel", "Error loading inspections", e)
                _inspections.value = emptyList()
                _filteredInspections.value = emptyList() // Ensure filtered list is also empty on error
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun deleteInspectionAndFiles(inspection: InspectionEntity) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                repository.deleteInspectionWithFiles(inspection)
                // Refresh the data from the repository after deletion
                allInspections = repository.getAllInspections()
                _inspections.value = allInspections
                applyFilters() // Re-apply filters to the updated list
                Log.i("HistoryViewModel", "Successfully deleted inspection and its files with ID: ${inspection.id}")
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete inspection and files: ${e.message}"
                Log.e("HistoryViewModel", "Error deleting inspection and files", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    @Deprecated("Use deleteInspectionAndFiles to ensure associated files are also deleted.")
    fun deleteInspections(inspectionIds: Set<Long>) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                repository.deleteInspections(inspectionIds)
                // Refresh the data from the repository
                allInspections = repository.getAllInspections()
                _inspections.value = allInspections
                applyFilters() // Re-apply filters after deletion
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete inspections: ${e.message}"
                Log.e("HistoryViewModel", "Error deleting inspections", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchInspections(query: String) {
        _searchQuery.value = query
        currentFilters["search"] = query
        applyFilters()
    }

    fun filterByField(fieldName: String, value: String) {
        val actualValue = if (value.isEmpty() && fieldName != "date") "All" else value
        currentFilters[fieldName] = actualValue
        when (fieldName) {
            "project" -> _filterProject.value = actualValue
            "inspectionType" -> _filterInspectionType.value = actualValue
            "date" -> _filterDate.value = actualValue // Date uses empty string for no filter
            "olt" -> _filterOlt.value = actualValue
            "fsa" -> _filterFsa.value = actualValue
            "asBuilt" -> _filterAsBuilt.value = actualValue
        }
        applyFilters()
    }

    private fun applyFilters() {
        var filteredList = allInspections

        // Apply search query first
        val query = _searchQuery.value ?: ""
        if (query.isNotEmpty()) {
            filteredList = filteredList.filter { inspection ->
                inspection.project.contains(query, ignoreCase = true) ||
                (inspection.olt?.contains(query, ignoreCase = true) == true) ||
                (inspection.fsa?.contains(query, ignoreCase = true) == true) ||
                (inspection.asBuilt?.contains(query, ignoreCase = true) == true) ||
                inspection.inspectionType.contains(query, ignoreCase = true) ||
                inspection.equipmentId.contains(query, ignoreCase = true) ||
                inspection.address.contains(query, ignoreCase = true) ||
                (inspection.drawing?.contains(query, ignoreCase = true) == true) ||
                (inspection.observations?.contains(query, ignoreCase = true) == true)
            }
        }

        // Apply field filters
        _filterProject.value?.takeIf { it != "All" }?.let { pj ->
            filteredList = filteredList.filter { it.project.equals(pj, ignoreCase = true) }
        }
        _filterInspectionType.value?.takeIf { it != "All" }?.let { type ->
            filteredList = filteredList.filter { it.inspectionType.equals(type, ignoreCase = true) }
        }
        _filterDate.value?.takeIf { it.isNotEmpty() }?.let { dateStr ->
            filteredList = filteredList.filter { it.date.contains(dateStr, ignoreCase = true) } // Assuming date is stored as YYYY-MM-DD string
        }
        _filterOlt.value?.takeIf { it != "All" }?.let { oltVal ->
            filteredList = filteredList.filter { it.olt?.equals(oltVal, ignoreCase = true) == true }
        }
        _filterFsa.value?.takeIf { it != "All" }?.let { fsaVal ->
            filteredList = filteredList.filter { it.fsa?.equals(fsaVal, ignoreCase = true) == true }
        }
        _filterAsBuilt.value?.takeIf { it != "All" }?.let { asBuiltVal ->
            filteredList = filteredList.filter { it.asBuilt?.equals(asBuiltVal, ignoreCase = true) == true }
        }
        
        // Apply sorting if any
        currentSortField?.let {
            _filteredInspections.value = sortList(filteredList, it, currentSortAscending) 
        } ?: run {
            _filteredInspections.value = filteredList
        }
    }

    private fun sortList(list: List<InspectionEntity>, fieldName: String, ascending: Boolean): List<InspectionEntity> {
        val sorted = list.sortedWith { a, b ->
            val comparison = when (fieldName) {
                "date" -> a.date.compareTo(b.date) // Ensure InspectionEntity.date is comparable
                "project" -> a.project.compareTo(b.project)
                "olt" -> (a.olt ?: "").compareTo(b.olt ?: "")
                "fsa" -> (a.fsa ?: "").compareTo(b.fsa ?: "")
                "asBuilt" -> (a.asBuilt ?: "").compareTo(b.asBuilt ?: "")
                "inspectionType" -> a.inspectionType.compareTo(b.inspectionType)
                "equipmentId" -> a.equipmentId.compareTo(b.equipmentId)
                // "address" -> a.address.compareTo(b.address) // Assuming address is comparable
                // "drawing" -> (a.drawing ?: "").compareTo(b.drawing ?: "")
                "timestamp" -> a.createdTimestamp.compareTo(b.createdTimestamp)
                else -> 0
            }
            if (ascending) comparison else -comparison
        }
        return sorted
    }

    fun sortBy(fieldName: String) {
        if (currentSortField == fieldName) {
            currentSortAscending = !currentSortAscending
        } else {
            currentSortField = fieldName
            currentSortAscending = true
        }
        // Re-apply filters which will also apply new sort order
        applyFilters()
        Log.i("HistoryViewModel", "Sorted by $fieldName (${if (currentSortAscending) "ascending" else "descending"})")
    }

    fun refreshInspections() {
        // Reset filters to default before loading, or ensure they are reapplied correctly by Fragment
        _searchQuery.value = ""
        _filterProject.value = "All"
        _filterInspectionType.value = "All"
        _filterDate.value = ""
        _filterOlt.value = "All"
        _filterFsa.value = "All"
        _filterAsBuilt.value = "All"
        
        currentFilters.clear()
        currentFilters["search"] = ""
        currentFilters["project"] = "All"
        currentFilters["inspectionType"] = "All"
        currentFilters["date"] = ""
        currentFilters["olt"] = "All"
        currentFilters["fsa"] = "All"
        currentFilters["asBuilt"] = "All"

        currentSortField = null // Reset sort order as well on refresh
        currentSortAscending = true

        loadInspections()
    }
}