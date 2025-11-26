import SwiftUI
import SwiftData

@main
struct InspectionApp: App {
    @Environment(\.scenePhase) private var scenePhase
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
        .modelContainer(for: Inspection.self)
        .onChange(of: scenePhase) { oldPhase, newPhase in
            if newPhase == .background {
                // Clean up temporary Excel files when app goes to background
                ExcelCacheManager.shared.cleanupAllFiles()
            }
        }
    }
}
