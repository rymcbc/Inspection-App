import SwiftUI

struct ContentView: View {
    var body: some View {
        TabView {
            InspectionFormView()
                .tabItem {
                    Label("New Inspection", systemImage: "square.and.pencil")
                }
            
            HistoryView()
                .tabItem {
                    Label("History", systemImage: "clock")
                }
        }
    }
}

#Preview {
    ContentView()
        .modelContainer(for: Inspection.self, inMemory: true)
}
