package com.qaassist.inspection.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.util.Log
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import com.qaassist.inspection.models.InspectionForm
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class PDFGenerator(private val context: Context) {
    
    private val locationUtils = LocationUtils(context)
    
    suspend fun generatePDF(form: InspectionForm, targetDir: File, fileName: String): File {
        val pdfFile = File(targetDir, "$fileName.pdf")
        
        try {
            val writer = PdfWriter(FileOutputStream(pdfFile))
            val pdfDocument = com.itextpdf.kernel.pdf.PdfDocument(writer)
            val document = Document(pdfDocument)
            
            // Add title
            document.add(
                Paragraph("QA ASSIST - QUALITY INSPECTION REPORT")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(18f)
                    .setBold()
            )
            
            document.add(Paragraph("\n"))
            
            // Add inspection details table
            val table = Table(UnitValue.createPercentArray(floatArrayOf(30f, 70f)))
            table.setWidth(UnitValue.createPercentValue(100f))
            
            addTableRow(table, "Date:", form.date)
            addTableRow(table, "Project:", form.project)
            addTableRow(table, "OLT:", form.olt)
            addTableRow(table, "FSA:", form.fsa)
            addTableRow(table, "AS-BUILT:", form.asBuilt)
            addTableRow(table, "Inspection Type:", form.inspectionType)
            addTableRow(table, "Equipment ID/Type:", form.equipmentId)
            addTableRow(table, "Address:", form.address)
            addTableRow(table, "Drawing:", form.drawing)
            addTableRow(table, "Observations:", form.observations)
            
            // Add GPS coordinates
            val coordinates = locationUtils.formatCoordinates(form.latitude, form.longitude)
            addTableRow(table, "GPS Coordinates:", coordinates)
            
            document.add(table)
            
            // Add map thumbnail if location is available
            if (form.latitude != null && form.longitude != null) {
                document.add(Paragraph("\n"))
                document.add(Paragraph("Location Map:").setBold())
                
                try {
                    val mapBitmap = generateMapThumbnail(form.latitude, form.longitude)
                    val mapImageData = bitmapToByteArray(mapBitmap)
                    val mapImage = Image(ImageDataFactory.create(mapImageData))
                    mapImage.setWidth(200f)
                    mapImage.setHeight(150f)
                    document.add(mapImage)
                } catch (e: Exception) {
                    document.add(Paragraph("Map thumbnail could not be generated"))
                }
            }
            
            // Add photos
            if (form.photos.isNotEmpty()) {
                document.add(Paragraph("\n"))
                document.add(Paragraph("Inspection Photos:").setBold())
                document.add(Paragraph("\n"))
                
                form.photos.forEach { photo ->
                    try {
                        val bitmap = BitmapFactory.decodeFile(photo.path)
                        if (bitmap != null) {
                            val resizedBitmap = resizeBitmap(bitmap, 400, 300)
                            val imageData = bitmapToByteArray(resizedBitmap)
                            val image = Image(ImageDataFactory.create(imageData))
                            image.setWidth(200f)
                            image.setHeight(150f)
                            document.add(image)
                            document.add(Paragraph("\n"))
                        }
                    } catch (e: Exception) {
                        Log.e("PDFGenerator", "Error adding photo: ${e.message}")
                    }
                }
            }
            
            document.close()
        } catch (e: Exception) {
            Log.e("PDFGenerator", "Error generating PDF: ${e.message}")
            throw e
        }
        
        return pdfFile
    }
    
    private fun addTableRow(table: Table, label: String, value: String) {
        table.addCell(Paragraph(label).setBold())
        table.addCell(Paragraph(value))
    }
    
    private fun generateMapThumbnail(latitude: Double, longitude: Double): Bitmap {
        // Create a simple map placeholder
        // In a real implementation, you would use Google Maps API or similar
        val bitmap = Bitmap.createBitmap(300, 200, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        
        // Background
        paint.color = Color.LTGRAY
        canvas.drawRect(0f, 0f, 300f, 200f, paint)
        
        // Grid lines
        paint.color = Color.GRAY
        paint.strokeWidth = 1f
        for (i in 0..10) {
            canvas.drawLine(i * 30f, 0f, i * 30f, 200f, paint)
            canvas.drawLine(0f, i * 20f, 300f, i * 20f, paint)
        }
        
        // Center point
        paint.color = Color.RED
        canvas.drawCircle(150f, 100f, 8f, paint)
        
        // Text
        paint.color = Color.BLACK
        paint.textSize = 12f
        canvas.drawText("Lat: ${"%.4f".format(latitude)}", 10f, 20f, paint)
        canvas.drawText("Lng: ${"%.4f".format(longitude)}", 10f, 35f, paint)
        
        return bitmap
    }
    
    private fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        
        val scaleWidth = maxWidth.toFloat() / width
        val scaleHeight = maxHeight.toFloat() / height
        val scale = minOf(scaleWidth, scaleHeight)
        
        val newWidth = (width * scale).toInt()
        val newHeight = (height * scale).toInt()
        
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
    
    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }
}