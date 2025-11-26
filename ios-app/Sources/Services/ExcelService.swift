import Foundation
import SwiftXLSX

struct ExcelService {
    /// Sanitizes a string for use in a filename by removing unsafe characters
    private static func sanitizeForFilename(_ input: String) -> String {
        let unsafeCharacters = CharacterSet(charactersIn: "/\\:*?\"<>|")
        return input.components(separatedBy: unsafeCharacters).joined(separator: "_")
    }
    
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
        
        // Save to temporary directory with sanitized filename
        let safeDate = sanitizeForFilename(inspection.date)
        let safeEquipmentId = sanitizeForFilename(inspection.equipmentId)
        let fileName = "Inspection_\(safeDate)_\(safeEquipmentId).xlsx"
        let tempDir = FileManager.default.temporaryDirectory
        let fileURL = tempDir.appendingPathComponent(fileName)
        
        // Save workbook - SwiftXLSX saves to current directory by default
        // SwiftXLSX's save method may save to the current working directory if only a filename is provided.
        // To ensure the file is saved to the desired location, use the full path if supported.
        let filePath = book.save(fileURL.path)
        
        // If the API does not support saving to a full path, filePath may be nil or not at the expected location.
        // Add error handling to check if the file exists at fileURL.
        if FileManager.default.fileExists(atPath: fileURL.path) {
            return fileURL
        } else if let savedPath = filePath {
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
                // Return nil if we can't move the file to temp directory
                return nil
            }
        }
        
        return nil
    }
}
