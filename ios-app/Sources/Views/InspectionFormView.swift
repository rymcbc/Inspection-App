import SwiftUI
import SwiftData

struct InspectionFormView: View {
    @Environment(\.modelContext) private var modelContext
    @Environment(\.dismiss) private var dismiss

    @State private var date: String
    @State private var project: String
    @State private var municipality: String
    @State private var olt: String
    @State private var fsa: String
    @State private var asBuilt: String
    @State private var inspectionType: String
    @State private var equipmentId: String
    @State private var address: String
    @State private var drawing: String
    @State private var observations: String
    @State private var latitude: Double?
    @State private var longitude: Double?

    private var existingInspection: Inspection?
    private var isEditing: Bool { existingInspection != nil }

    init(inspection: Inspection? = nil) {
        self.existingInspection = inspection
        _date = State(initialValue: inspection?.date ?? Self.currentDateString())
        _project = State(initialValue: inspection?.project ?? "")
        _municipality = State(initialValue: inspection?.municipality ?? "")
        _olt = State(initialValue: inspection?.olt ?? "")
        _fsa = State(initialValue: inspection?.fsa ?? "")
        _asBuilt = State(initialValue: inspection?.asBuilt ?? "")
        _inspectionType = State(initialValue: inspection?.inspectionType ?? "")
        _equipmentId = State(initialValue: inspection?.equipmentId ?? "")
        _address = State(initialValue: inspection?.address ?? "")
        _drawing = State(initialValue: inspection?.drawing ?? "")
        _observations = State(initialValue: inspection?.observations ?? "")
        _latitude = State(initialValue: inspection?.latitude)
        _longitude = State(initialValue: inspection?.longitude)
    }

    var body: some View {
        Form {
            Section(header: Text("Project Info")) {
                TextField("Date", text: $date)
                    .disabled(true)
                TextField("Project", text: $project)
                TextField("Municipality", text: $municipality)
                HStack {
                    TextField("OLT", text: $olt)
                    TextField("FSA", text: $fsa)
                }
            }

            Section(header: Text("Details")) {
                TextField("AS-BUILT", text: $asBuilt)
                TextField("Inspection Type", text: $inspectionType)
                TextField("Equipment ID/Type", text: $equipmentId)
                HStack {
                    TextField("Address", text: $address)
                    TextField("Drawing", text: $drawing)
                }
            }

            Section(header: Text("Observations")) {
                TextEditor(text: $observations)
                    .frame(minHeight: 100)
            }

            Section(header: Text("Location")) {
                Button(action: captureLocation) {
                    HStack {
                        Image(systemName: "location.fill")
                        Text("Capture Location")
                    }
                }
                if let lat = latitude, let lon = longitude {
                    Text("Lat: \(lat, specifier: "%.6f"), Lon: \(lon, specifier: "%.6f")")
                        .font(.caption)
                        .foregroundColor(.secondary)
                }
            }

            Section(header: Text("Photos")) {
                Button(action: takePhoto) {
                    HStack {
                        Image(systemName: "camera.fill")
                        Text("Add Picture")
                    }
                }
                Button(action: selectPhoto) {
                    HStack {
                        Image(systemName: "photo.on.rectangle")
                        Text("Select from Library")
                    }
                }
            }
        }
        .navigationTitle(isEditing ? "Edit Inspection" : "New Inspection")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .navigationBarTrailing) {
                Button("Save") {
                    saveInspection()
                }
            }
        }
    }

    private static func currentDateString() -> String {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"
        return formatter.string(from: Date())
    }

    private func captureLocation() {
        // Placeholder for location capture
        // TODO: Implement CoreLocation integration
    }

    private func takePhoto() {
        // Placeholder for camera capture
        // TODO: Implement UIImagePickerController or PhotosUI
    }

    private func selectPhoto() {
        // Placeholder for photo library selection
        // TODO: Implement PhotosPicker
    }

    private func saveInspection() {
        if let inspection = existingInspection {
            inspection.date = date
            inspection.project = project
            inspection.municipality = municipality
            inspection.olt = olt
            inspection.fsa = fsa
            inspection.asBuilt = asBuilt
            inspection.inspectionType = inspectionType
            inspection.equipmentId = equipmentId
            inspection.address = address
            inspection.drawing = drawing
            inspection.observations = observations
            inspection.latitude = latitude
            inspection.longitude = longitude
        } else {
            let newInspection = Inspection(
                date: date,
                project: project,
                municipality: municipality,
                olt: olt,
                fsa: fsa,
                asBuilt: asBuilt,
                inspectionType: inspectionType,
                equipmentId: equipmentId,
                address: address,
                drawing: drawing,
                observations: observations,
                latitude: latitude,
                longitude: longitude
            )
            modelContext.insert(newInspection)
        }
        try? modelContext.save()
        dismiss()
    }
}

#Preview {
    NavigationStack {
        InspectionFormView()
    }
    .modelContainer(for: Inspection.self, inMemory: true)
}
