package com.qaassist.inspection.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PhotoModel(
    val path: String
): Parcelable
