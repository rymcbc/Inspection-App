import SwiftUI
import SwiftData

struct InspectionFormView: View {
    @Environment(\.modelContext) private var modelContext
    
    @State private var date: String = ""
    @State private var project: String = ""
    @State private var municipality: String = ""
    @State private var olt: String = ""
    @State private var fsa: String = ""
    @State private var asBuilt: String = ""
    @State private var inspectionType: String = ""
    @State private var equipmentId: String = ""
    @State private var address: String = ""
    @State private var drawing: String = ""
    @State private var observations: String = ""
    @State private var latitude: Double?
    @State private var longitude: Double?
    
    @State private var showingSaveAlert = false
    @State private var alertMessage = ""
    @State private var isSaving = false
    
    /// Validates that required fields are filled
    private var isFormValid: Bool {
        !date.trimmingCharacters(in: .whitespaces).isEmpty &&
        !project.trimmingCharacters(in: .whitespaces).isEmpty &&
        !equipmentId.trimmingCharacters(in: .whitespaces).isEmpty
    }
    
    var body: some View {
        NavigationStack {
            Form {
                Section(header: Text("Basic Information")) {
                    TextField("Date *", text: $date)
                    TextField("Project *", text: $project)
                    TextField("Municipality", text: $municipality)
                    TextField("OLT", text: $olt)
                    TextField("FSA", text: $fsa)
                }
                
                Section(header: Text("Inspection Details")) {
                    TextField("As-Built", text: $asBuilt)
                    TextField("Inspection Type", text: $inspectionType)
                    TextField("Equipment ID *", text: $equipmentId)
                    TextField("Address", text: $address)
                    TextField("Drawing", text: $drawing)
                }
                
                Section(header: Text("Observations")) {
                    TextEditor(text: $observations)
                        .frame(minHeight: 100)
                }
                
                Section(header: Text("Location")) {
                    if let lat = latitude, let lon = longitude {
                        Text("Latitude: \(lat, specifier: "%.6f")")
                        Text("Longitude: \(lon, specifier: "%.6f")")
                    } else {
                        Text("No location captured")
                            .foregroundColor(.secondary)
                    }
                    
                    Button(action: getLocation) {
                        Label("Get Location", systemImage: "location")
                    }
                }
                
                Section(header: Text("Photos")) {
                    Button(action: capturePhoto) {
                        Label("Capture Photo", systemImage: "camera")
                    }
                }
                
                Section {
                    Button(action: saveInspection) {
                        if isSaving {
                            ProgressView()
                                .frame(maxWidth: .infinity)
                        } else {
                            Label("Save Inspection", systemImage: "square.and.arrow.down")
                        }
                    }
                    .frame(maxWidth: .infinity)
                    .disabled(!isFormValid || isSaving)
                }
                
                if !isFormValid {
                    Section {
                        Text("* Required fields must be filled")
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                }
            }
            .navigationTitle("New Inspection")
            .alert("Save Result", isPresented: $showingSaveAlert) {
                Button("OK", role: .cancel) { }
            } message: {
                Text(alertMessage)
            }
        }
    }
    
    private func saveInspection() {
        guard isFormValid, !isSaving else { return }
        
        isSaving = true
        
        let inspection = Inspection(
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
        
        modelContext.insert(inspection)
        
        do {
            try modelContext.save()
            alertMessage = "Inspection saved successfully!"
            clearForm()
        } catch {
            alertMessage = "Error saving inspection: \(error.localizedDescription)"
        }
        
        isSaving = false
        showingSaveAlert = true
    }
    
    private func clearForm() {
        date = ""
        project = ""
        municipality = ""
        olt = ""
        fsa = ""
        asBuilt = ""
        inspectionType = ""
        equipmentId = ""
        address = ""
        drawing = ""
        observations = ""
        latitude = nil
        longitude = nil
    }
    
    private func capturePhoto() {
        // Placeholder for camera functionality
        // TODO: Implement camera capture using UIImagePickerController or PHPickerViewController
    }
    
    private func getLocation() {
        // Placeholder for location functionality
        // TODO: Implement location capture using CoreLocation
    }
}

#Preview {
    InspectionFormView()
        .modelContainer(for: Inspection.self, inMemory: true)
}
