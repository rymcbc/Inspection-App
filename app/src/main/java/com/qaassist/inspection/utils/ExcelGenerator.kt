package com.qaassist.inspection.utils

import android.content.Context
import android.net.Uri
import com.qaassist.inspection.models.InspectionForm
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileOutputStream

object ExcelGenerator {

    fun generateExcel(
        context: Context,
        form: InspectionForm,
        outputUri: Uri
    ): Uri? {
        try {
            val workbook: Workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("Inspection Data")

            // Header Row
            val headerRow = sheet.createRow(0)
            headerRow.createCell(0).setCellValue("Project")
            headerRow.createCell(1).setCellValue("Municipality")
            headerRow.createCell(2).setCellValue("Date")
            headerRow.createCell(3).setCellValue("OLT")
            headerRow.createCell(4).setCellValue("FSA")
            headerRow.createCell(5).setCellValue("As-Built")
            headerRow.createCell(6).setCellValue("Inspection Type")
            headerRow.createCell(7).setCellValue("Equipment ID")
            headerRow.createCell(8).setCellValue("Address")
            headerRow.createCell(9).setCellValue("Drawing")
            headerRow.createCell(10).setCellValue("Observations")

            // Data Row
            val dataRow = sheet.createRow(1)
            dataRow.createCell(0).setCellValue(form.project)
            dataRow.createCell(1).setCellValue(form.municipality)
            dataRow.createCell(2).setCellValue(form.date)
            dataRow.createCell(3).setCellValue(form.olt)
            dataRow.createCell(4).setCellValue(form.fsa)
            dataRow.createCell(5).setCellValue(form.asBuilt)
            dataRow.createCell(6).setCellValue(form.inspectionType)
            dataRow.createCell(7).setCellValue(form.equipmentId)
            dataRow.createCell(8).setCellValue(form.address)
            dataRow.createCell(9).setCellValue(form.drawing)
            dataRow.createCell(10).setCellValue(form.observations)

            context.contentResolver.openFileDescriptor(outputUri, "w")?.use {
                FileOutputStream(it.fileDescriptor).use { outputStream ->
                    workbook.write(outputStream)
                }
            }
            workbook.close()
            return outputUri
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}