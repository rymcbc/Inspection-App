package com.qaassist.inspection.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class InspectionForm(
    val date: String,
    val project: String,
    val municipality: String,
    val olt: String,
    val fsa: String,
    val asBuilt: String,
    val inspectionType: String,
    val equipmentId: String,
    val address: String,
    val drawing: String,
    val observations: String,
    val status: String,
    val photos: List<PhotoModel> = emptyList(),
    val latitude: Double? = null,
    val longitude: Double? = null,
    val excelPath: String? = null,
    val excelUri: String? = null
) : Parcelable

fun InspectionForm.isValid(isCustomProject: Boolean, isCustomMunicipality: Boolean): Boolean {
    val projectsToExcludeOltFsaValidation = listOf("5080572", "5080585")

    var basicFieldsValid = project.isNotBlank() &&
            asBuilt.isNotBlank() &&
            inspectionType.isNotBlank() &&
            equipmentId.isNotBlank() &&
            address.isNotBlank() &&
            drawing.isNotBlank() &&
            status.isNotBlank()

    // Conditionally validate OLT and FSA
    if (project !in projectsToExcludeOltFsaValidation) {
        basicFieldsValid = basicFieldsValid && olt.isNotBlank() && fsa.isNotBlank()
    }

    // Municipality validation: must be non-blank whether it's from dropdown or custom field.
    basicFieldsValid = basicFieldsValid && municipality.isNotBlank()

    return basicFieldsValid
}
