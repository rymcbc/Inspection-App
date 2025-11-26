import SwiftUI
import SwiftData

struct ContentView: View {
    @Environment(\.modelContext) private var modelContext
    @Query(sort: \Inspection.createdTimestamp, order: .reverse) private var inspections: [Inspection]

    var body: some View {
        NavigationStack {
            List {
                ForEach(inspections) { inspection in
                    NavigationLink(destination: InspectionFormView(inspection: inspection)) {
                        VStack(alignment: .leading, spacing: 4) {
                            Text(inspection.project)
                                .font(.headline)
                            Text(inspection.date)
                                .font(.subheadline)
                                .foregroundColor(.secondary)
                            if !inspection.address.isEmpty {
                                Text(inspection.address)
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                            }
                        }
                        .padding(.vertical, 4)
                    }
                }
                .onDelete(perform: deleteInspections)
            }
            .navigationTitle("Inspections")
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    NavigationLink(destination: InspectionFormView()) {
                        Image(systemName: "plus")
                    }
                }
            }
            .overlay {
                if inspections.isEmpty {
                    ContentUnavailableView(
                        "No Inspections",
                        systemImage: "clipboard",
                        description: Text("Tap + to create a new inspection")
                    )
                }
            }
        }
    }

    private func deleteInspections(offsets: IndexSet) {
        for index in offsets {
            modelContext.delete(inspections[index])
        }
    }
}

#Preview {
    ContentView()
        .modelContainer(for: Inspection.self, inMemory: true)
}
