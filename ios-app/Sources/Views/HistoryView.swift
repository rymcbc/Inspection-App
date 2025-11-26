import SwiftUI
import SwiftData

struct HistoryView: View {
    @Query(sort: \Inspection.createdTimestamp, order: .reverse) private var inspections: [Inspection]
    @Environment(\.modelContext) private var modelContext
    
    @State private var showingExportError = false
    @State private var exportErrorMessage = ""
    
    var body: some View {
        NavigationStack {
            List {
                ForEach(inspections) { inspection in
                    InspectionRow(inspection: inspection)
                }
                .onDelete(perform: deleteInspections)
            }
            .navigationTitle("Inspection History")
            .overlay {
                if inspections.isEmpty {
                    ContentUnavailableView(
                        "No Inspections",
                        systemImage: "doc.text",
                        description: Text("Saved inspections will appear here.")
                    )
                }
            }
            .alert("Export Error", isPresented: $showingExportError) {
                Button("OK", role: .cancel) { }
            } message: {
                Text(exportErrorMessage)
            }
        }
    }
    
    private func deleteInspections(at offsets: IndexSet) {
        for index in offsets {
            modelContext.delete(inspections[index])
        }
    }
}

struct InspectionRow: View {
    let inspection: Inspection
    
    @State private var exportedURL: URL?
    @State private var isExporting = false
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                Text(inspection.date)
                    .font(.headline)
                Spacer()
                Text(inspection.project)
                    .font(.subheadline)
                    .foregroundColor(.secondary)
            }
            
            Text("OLT: \(inspection.olt) | FSA: \(inspection.fsa)")
                .font(.caption)
                .foregroundColor(.secondary)
            
            Text(inspection.address)
                .font(.caption)
                .lineLimit(1)
            
            HStack {
                Text("Equipment: \(inspection.equipmentId)")
                    .font(.caption2)
                    .foregroundColor(.secondary)
                
                Spacer()
                
                if let url = exportedURL {
                    ShareLink(item: url) {
                        Label("Share", systemImage: "square.and.arrow.up")
                            .font(.caption)
                    }
                } else {
                    Button(action: {
                        if let url = ExcelService.generateExcel(inspection: inspection) {
                            exportedURL = url
                        }
                    }) {
                        Label("Export Excel", systemImage: "doc.text")
                            .font(.caption)
                    }
                    .buttonStyle(.bordered)
                }
            }
        }
        .padding(.vertical, 4)
    }
}

#Preview {
    HistoryView()
        .modelContainer(for: Inspection.self, inMemory: true)
}
