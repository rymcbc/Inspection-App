package com.qaassist.inspection.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    
    private val _permissionsGranted = MutableLiveData<Boolean>()
    val permissionsGranted: LiveData<Boolean> = _permissionsGranted
    
    private val _currentTab = MutableLiveData<Int>()
    val currentTab: LiveData<Int> = _currentTab
    
    fun setPermissionsGranted(granted: Boolean) {
        _permissionsGranted.value = granted
    }
    
    fun setCurrentTab(tab: Int) {
        _currentTab.value = tab
    }
}