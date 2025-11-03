package com.qaassist.inspection.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import java.io.File
import java.io.IOException
import android.os.Environment

object FileUtils {

    private const val ROOT_DIRECTORY = "QA-Assist"

    @Throws(IOException::class)
    fun copyPhotosToDirectory(context: Context, photoUris: List<String>, relativePath: String, displayNames: List<String>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            copyPhotosToDirectoryQ(context, photoUris, relativePath, displayNames)
        } else {
            copyPhotosToDirectoryLegacy(context, photoUris, relativePath, displayNames)
        }
    }

    @Throws(IOException::class)
    private fun copyPhotosToDirectoryQ(context: Context, photoUris: List<String>, relativePath: String, displayNames: List<String>) {
        val contentResolver = context.contentResolver
        val finalRelativePath = "${Environment.DIRECTORY_DOCUMENTS}/$ROOT_DIRECTORY/$relativePath"

        for ((index, uriString) in photoUris.withIndex()) {
            val photoUri = Uri.parse(uriString)
            val displayName = displayNames[index]

            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, finalRelativePath)
            }

            val destUri = contentResolver.insert(MediaStore.Files.getContentUri("external"), values)
                ?: throw IOException("Failed to create new MediaStore entry for $displayName")

            try {
                contentResolver.openInputStream(photoUri)?.use { inputStream ->
                    contentResolver.openOutputStream(destUri)?.use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            } catch (e: Exception) {
                contentResolver.delete(destUri, null, null)
                throw IOException("Failed to copy photo $displayName: ${e.message}", e)
            }
        }
    }

    @Throws(IOException::class)
    private fun copyPhotosToDirectoryLegacy(context: Context, photoUris: List<String>, relativePath: String, displayNames: List<String>) {
        val documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val qaAssistDir = File(documentsDir, ROOT_DIRECTORY)
        val targetDir = File(qaAssistDir, relativePath)

        if (!targetDir.exists() && !targetDir.mkdirs()) {
            throw IOException("Failed to create directory: ${targetDir.absolutePath}")
        }
        
        for ((index, uriString) in photoUris.withIndex()) {
            val photoUri = Uri.parse(uriString)
            val displayName = displayNames[index]
            val destFile = File(targetDir, displayName)

            try {
                context.contentResolver.openInputStream(photoUri)?.use { inputStream ->
                    destFile.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            } catch (e: Exception) {
                throw IOException("Failed to copy photo $displayName: ${e.message}", e)
            }
        }
    }
}