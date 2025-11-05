package com.qaassist.inspection.utils

import android.content.Context
import android.net.Uri
import com.qaassist.inspection.models.InspectionForm
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.util.AreaReference
import org.apache.poi.ss.util.CellReference
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFFont
import org.apache.poi.xssf.usermodel.XSSFTable
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileOutputStream

object ExcelGenerator {

    fun generateExcel(context: Context, form: InspectionForm, outputUri: Uri) {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Inspection Details")

        // Header Style
        val headerStyle: XSSFCellStyle = workbook.createCellStyle().apply {
            fillForegroundColor = IndexedColors.GREY_25_PERCENT.getIndex()
            fillPattern = FillPatternType.SOLID_FOREGROUND
            alignment = HorizontalAlignment.CENTER
            borderBottom = BorderStyle.THIN
        }
        val headerFont: XSSFFont = workbook.createFont().apply {
            color = IndexedColors.WHITE.getIndex()
            bold = true
        }
        headerStyle.setFont(headerFont)

        // Data Style
        val dataStyle: XSSFCellStyle = workbook.createCellStyle().apply {
            alignment = HorizontalAlignment.LEFT
        }

        // Header
        val headers = listOf("DATE", "PROJECT", "MUNICIPALITY", "OLT", "FSA", "AS-BUILT",
            "INSPECTION TYPE", "EQUIPMENT ID", "ADDRESS", "DRAWING", "OBSERVATIONS")
        val headerRow = sheet.createRow(0)
        headers.forEachIndexed { index, header ->
            val cell = headerRow.createCell(index)
            cell.setCellValue(header)
            cell.cellStyle = headerStyle
        }

        // Data
        val dataRow = sheet.createRow(1)
        val data = listOf(form.date, form.project, form.municipality, form.olt, form.fsa, form.asBuilt,
            form.inspectionType, form.equipmentId, form.address, form.drawing, form.observations)
        data.forEachIndexed { index, value ->
            val cell = dataRow.createCell(index)
            cell.setCellValue(value)
            cell.cellStyle = dataStyle
        }

        // Manually set column widths to avoid crash
        headers.forEachIndexed { index, header ->
            var width = ((header.length + 5) * 256).coerceAtMost(255 * 256)
            // Make the date column wider
            if (index == 0) {
                width = ((form.date.length + 10) * 256).coerceAtMost(255 * 256)
            }
            sheet.setColumnWidth(index, width)
        }
        
        // Create Table
        val area = AreaReference(CellReference(0, 0), CellReference(1, headers.size - 1), workbook.spreadsheetVersion)
        val table: XSSFTable = sheet.createTable(area)
        table.name = "InspectionData"
        table.displayName = "InspectionDataTable"
        
        // Apply a default table style and enable filter buttons
        table.ctTable.addNewAutoFilter().ref = area.formatAsString()
        table.ctTable.addNewTableStyleInfo().setName("TableStyleMedium2")

        try {
            context.contentResolver.openFileDescriptor(outputUri, "w")?.use {
                FileOutputStream(it.fileDescriptor).use { outputStream ->
                    workbook.write(outputStream)
                }
            }
        } finally {
            workbook.close()
        }
    }
}
