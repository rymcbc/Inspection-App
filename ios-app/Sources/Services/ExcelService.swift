import Foundation
import SwiftXLSX

/// Service for generating Excel files from inspection data
class ExcelService {
    /// Generates an Excel file for the given inspection
    /// - Parameter inspection: The inspection to export
    /// - Returns: URL to the generated Excel file, or nil if generation fails
    static func generateExcel(inspection: Inspection) -> URL? {
        // Create a new workbook
        let book = XWorkBook()

        // Create a sheet
        let sheet = book.NewSheet("Inspection")

        // Add headers
        let headers = [
            "Date", "Project", "Municipality", "OLT", "FSA",
            "AS-BUILT", "Inspection Type", "Equipment ID", "Address",
            "Drawing", "Observations", "Latitude", "Longitude"
        ]

        for (index, header) in headers.enumerated() {
            let cell = sheet.AddCell(XCoords(row: 1, col: index + 1))
            cell.value = .text(header)
        }

        // Add data row
        let dataRow = 2
        let values: [String] = [
            inspection.date,
            inspection.project,
            inspection.municipality,
            inspection.olt,
            inspection.fsa,
            inspection.asBuilt,
            inspection.inspectionType,
            inspection.equipmentId,
            inspection.address,
            inspection.drawing,
            inspection.observations,
            inspection.latitude.map { String($0) } ?? "",
            inspection.longitude.map { String($0) } ?? ""
        ]

        for (index, value) in values.enumerated() {
            let cell = sheet.AddCell(XCoords(row: dataRow, col: index + 1))
            cell.value = .text(value)
        }

        // Save to documents directory
        guard let documentsPath = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first else {
            return nil
        }

        let fileName = "Inspection_\(inspection.date)_\(inspection.project).xlsx"
            .replacingOccurrences(of: " ", with: "_")
        let filePath = documentsPath.appendingPathComponent(fileName)

        // Write the file
        let result = book.save(filePath.path)
        return result ? filePath : nil
    }
}
