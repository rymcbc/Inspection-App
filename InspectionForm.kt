package com.qaassist.inspection.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class InspectionForm(
    val date: String,
    val project: String,
    val olt: String,
    val fsa: String,
    val asBuilt: String,
    val inspectionType: String,
    val equipmentId: String,
    val address: String,
    val drawing: String,
    val observations: String,
    val photos: List<PhotoItem> = emptyList(),
    val latitude: Double? = null,
    val longitude: Double? = null,
    val pdfPath: String? = null
) : Parcelable

fun InspectionForm.isValid(): Boolean {
    return project.isNotBlank() &&
           olt.isNotBlank() &&
           fsa.isNotBlank() &&
           asBuilt.isNotBlank() &&
           inspectionType.isNotBlank() &&
           equipmentId.isNotBlank() &&
           address.isNotBlank() &&
           drawing.isNotBlank()
}