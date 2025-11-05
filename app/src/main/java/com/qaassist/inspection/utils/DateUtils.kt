package com.qaassist.inspection.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayDateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    private val timestampFormatter = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())

    fun getCurrentDate(): String {
        return dateFormatter.format(Date())
    }
    
    fun getCurrentDisplayDate(): String {
        return displayDateFormatter.format(Date())
    }
    
    fun getCurrentTimestamp(): String {
        return timestampFormatter.format(Date())
    }
    
    fun formatDisplayDate(dateString: String): String {
        return try {
            val date = dateFormatter.parse(dateString)
            date?.let { displayDateFormatter.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }
    
    fun formatTimestamp(timestamp: Long): String {
        return displayDateFormatter.format(Date(timestamp))
    }
    
    fun isValidDate(dateString: String): Boolean {
        return try {
            dateFormatter.parse(dateString) != null
        } catch (e: Exception) {
            false
        }
    }
}