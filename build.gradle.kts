// Top-level build file where you can add configuration options common to all sub-modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.navigation.safeargs) apply false
    alias(libs.plugins.secrets) apply false
}

// Define common properties for all modules
extra.apply {
    set("compileSdkVersion", 35)
    set("targetSdkVersion", 35)
    set("minSdkVersion", 24)
    set("buildToolsVersion", "35.0.0")
}

tasks.register("clean", Delete::class) {
    delete(layout.buildDirectory)
}

// Task to check for dependency updates
tasks.register("dependencyUpdates") {
    group = "verification"
    description = "Check for dependency updates across all modules"
    
    doLast {
        println("═══════════════════════════════════════")
        println("🔍 DEPENDENCY UPDATE CHECK")
        println("═══════════════════════════════════════")
        println("Current build time: ${java.time.LocalDateTime.now()}")
        println("To update dependencies:")
        println("1. Check version catalog: gradle/libs.versions.toml")
        println("2. Update versions to latest stable releases")
        println("3. Test thoroughly after updates")
        println("═══════════════════════════════════════")
    }
}

// Task to generate dependency report
tasks.register("dependencyReport") {
    group = "verification"
    description = "Generate dependency report for security analysis"
    
    doLast {
        println("📊 Generating dependency report...")
        println("Run: ./gradlew app:dependencies > dependency-report.txt")
    }
}