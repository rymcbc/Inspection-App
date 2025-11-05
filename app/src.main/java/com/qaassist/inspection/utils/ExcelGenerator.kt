// ...existing code...
import org.apache.poi.ss.util.AreaReference
import org.apache.poi.ss.util.CellReference
import org.apache.poi.xssf.usermodel.XSSFTable
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileOutputStream

object ExcelGenerator {

// ...existing code...
            dataRow.createCell(9).setCellValue(form.drawing)
            dataRow.createCell(10).setCellValue(form.observations)

            // Create Table
            val area = AreaReference(CellReference(0, 0), CellReference(1, headers.size - 1), workbook.spreadsheetVersion)
            val table: XSSFTable = sheet.createTable(area)
            table.name = "InspectionData"
            table.displayName = "InspectionDataTable"

            context.contentResolver.openFileDescriptor(outputUri, "w")?.use {
                FileOutputStream(it.fileDescriptor).use { outputStream ->
                    workbook.write(outputStream)
// ...existing code...

