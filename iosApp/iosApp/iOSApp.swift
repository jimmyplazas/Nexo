import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    
    init() {
        initKoinKt.doInitKoin()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
