import SwiftUI
import SwiftData

@main
struct InspectionApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
        .modelContainer(for: Inspection.self)
    }
}
