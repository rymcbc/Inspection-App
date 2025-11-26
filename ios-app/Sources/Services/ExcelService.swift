import Foundation
import SwiftXLSX

struct ExcelService {
    static func generateExcel(inspection: Inspection) -> URL? {
        let book = XWorkBook()
        let sheet = book.NewSheet("Inspection Details")
        
        // Headers - matching Android exactly
        let headers = ["DATE", "PROJECT", "MUNICIPALITY", "OLT", "FSA", "AS-BUILT",
                       "INSPECTION TYPE", "EQUIPMENT ID", "ADDRESS", "DRAWING", "OBSERVATIONS"]
        
        // Create header row
        for (index, header) in headers.enumerated() {
            let cell = sheet.AddCell(XCoords(row: 1, col: index + 1))
            cell.value = .text(header)
            cell.Font = XFont(.TrebuchetMS, 12, isBold: true)
        }
        
        // Create data row
        let data = [
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
            inspection.observations
        ]
        
        for (index, value) in data.enumerated() {
            let cell = sheet.AddCell(XCoords(row: 2, col: index + 1))
            cell.value = .text(value)
        }
        
        // Save to temporary directory
        let fileName = "Inspection_\(inspection.date.replacingOccurrences(of: "/", with: "-"))_\(inspection.equipmentId).xlsx"
        let tempDir = FileManager.default.temporaryDirectory
        let fileURL = tempDir.appendingPathComponent(fileName)
        
        // Save workbook
        let filePath = book.save(fileName)
        
        // Move from default save location to our desired location if needed
        if let savedPath = filePath {
            let savedURL = URL(fileURLWithPath: savedPath)
            do {
                // Remove existing file if present
                if FileManager.default.fileExists(atPath: fileURL.path) {
                    try FileManager.default.removeItem(at: fileURL)
                }
                try FileManager.default.moveItem(at: savedURL, to: fileURL)
                return fileURL
            } catch {
                print("Error moving Excel file: \(error)")
                return savedURL
            }
        }
        
        return nil
    }
}
