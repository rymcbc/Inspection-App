import Foundation
import SwiftData

@Model
class Inspection {
    var date: String
    var project: String
    var municipality: String
    var olt: String
    var fsa: String
    var asBuilt: String
    var inspectionType: String
    var equipmentId: String
    var address: String
    var drawing: String
    var observations: String
    var latitude: Double?
    var longitude: Double?
    var excelPath: String
    var excelUri: String
    var photosPaths: String
    
    init(
        date: String,
        project: String,
        municipality: String,
        olt: String,
        fsa: String,
        asBuilt: String,
        inspectionType: String,
        equipmentId: String,
        address: String,
        drawing: String,
        observations: String,
        latitude: Double? = nil,
        longitude: Double? = nil,
        excelPath: String = "",
        excelUri: String = "",
        photosPaths: String = ""
    ) {
        self.date = date
        self.project = project
        self.municipality = municipality
        self.olt = olt
        self.fsa = fsa
        self.asBuilt = asBuilt
        self.inspectionType = inspectionType
        self.equipmentId = equipmentId
        self.address = address
        self.drawing = drawing
        self.observations = observations
        self.latitude = latitude
        self.longitude = longitude
        self.excelPath = excelPath
        self.excelUri = excelUri
        self.photosPaths = photosPaths
    }
}
