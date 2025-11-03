package com.qaassist.inspection.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PhotoItem(
    val path: String,
    val uri: Uri,
    val timestamp: Long,
    val isSelected: Boolean = false
) : Parcelable