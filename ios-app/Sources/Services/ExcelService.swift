import Foundation
import SwiftXLSX

/// Manages temporary Excel file cache and cleanup
class ExcelCacheManager {
    static let shared = ExcelCacheManager()
    
    private var cachedURLs: [String: URL] = [:]
    private let cacheQueue = DispatchQueue(label: "com.inspectionapp.excelcache")
    
    private init() {}
    
    /// Gets cached URL for an inspection ID, or nil if not cached
    func getCachedURL(for inspectionId: String) -> URL? {
        cacheQueue.sync {
            if let url = cachedURLs[inspectionId], FileManager.default.fileExists(atPath: url.path) {
                return url
            }
            return nil
        }
    }
    
    /// Caches a URL for an inspection ID
    func cacheURL(_ url: URL, for inspectionId: String) {
        cacheQueue.sync {
            cachedURLs[inspectionId] = url
        }
    }
    
    /// Cleans up all temporary Excel files
    func cleanupAllFiles() {
        cacheQueue.sync {
            for (_, url) in cachedURLs {
                try? FileManager.default.removeItem(at: url)
            }
            cachedURLs.removeAll()
        }
        
        // Also clean up any orphaned inspection Excel files in temp directory
        let tempDir = FileManager.default.temporaryDirectory
        if let files = try? FileManager.default.contentsOfDirectory(at: tempDir, includingPropertiesForKeys: nil) {
            for file in files where file.lastPathComponent.hasPrefix("Inspection_") && file.pathExtension == "xlsx" {
                try? FileManager.default.removeItem(at: file)
            }
        }
    }
    
    /// Removes cached URL for a specific inspection
    func removeCachedURL(for inspectionId: String) {
        cacheQueue.sync {
            if let url = cachedURLs.removeValue(forKey: inspectionId) {
                try? FileManager.default.removeItem(at: url)
            }
        }
    }
}

struct ExcelService {
    /// Sanitizes a string for use in a filename by removing unsafe characters
    private static func sanitizeForFilename(_ input: String) -> String {
        let unsafeCharacters = CharacterSet(charactersIn: "/\\:*?\"<>|")
        return input.components(separatedBy: unsafeCharacters).joined(separator: "_")
    }
    
    /// Generates an Excel file for the inspection, using cache if available
    static func generateExcel(inspection: Inspection) -> URL? {
        // Use createdTimestamp as unique identifier for caching
        let inspectionId = String(inspection.createdTimestamp)
        
        // Check cache first
        if let cachedURL = ExcelCacheManager.shared.getCachedURL(for: inspectionId) {
            return cachedURL
        }
        
        // Generate new file
        guard let url = createExcelFile(for: inspection) else {
            return nil
        }
        
        // Cache the URL
        ExcelCacheManager.shared.cacheURL(url, for: inspectionId)
        return url
    }
    
    private static func createExcelFile(for inspection: Inspection) -> URL? {
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
