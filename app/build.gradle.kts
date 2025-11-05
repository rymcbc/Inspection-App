import java.time.LocalDateTime

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.navigation.safeargs)
}

android {
    namespace = "com.qaassist.inspection"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.qaassist.inspection"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro"
            )
            
            // Enable R8 optimizations
            isDebuggable = false
            isJniDebuggable = false
        }
        debug {
            isMinifyEnabled = false
            isDebuggable = true
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-DEBUG"
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }
    
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-Xjsr305=strict"
        )
    }
    
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/DEPENDENCIES"
            excludes += "/META-INF/LICENSE*"
            excludes += "/META-INF/NOTICE*"
            excludes += "/META-INF/ASL2.0"
            excludes += "**/*.kotlin_module"
            excludes += "**/*.version"
        }
    }
    
    lint {
        checkReleaseBuilds = false
        abortOnError = false
        baseline = file("lint-baseline.xml")
        disable += "MissingTranslation"
        disable += "ExtraTranslation"
        warningsAsErrors = false
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
        animationsDisabled = true
    }
}

// Room schema export directory
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
}

dependencies {
    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    
    // Activity & Fragment
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)
    
    // Navigation
    implementation(libs.bundles.navigation)
    
    // UI Components
    implementation(libs.androidx.viewpager2)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.recyclerview.selection)
    implementation(libs.androidx.swiperefreshlayout)
    
    // Lifecycle & Architecture
    implementation(libs.bundles.lifecycle)
    
    // Room Database
    implementation(libs.bundles.room)
    ksp(libs.androidx.room.compiler)
    
    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)
    
    // Camera
    implementation(libs.bundles.camera)
    
    // Location Services
    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)
    
    // Excel Generation
    implementation("org.apache.poi:poi:5.2.3")
    implementation("org.apache.poi:poi-ooxml:5.2.3")
    
    // Image Loading
    implementation(libs.bundles.glide)
    ksp(libs.glide.compiler)
    
    // Serialization
    implementation(libs.gson)
    implementation(libs.kotlinx.serialization.json)
    
    // Coroutines
    implementation(libs.bundles.coroutines)
    
    // Security
    implementation(libs.androidx.security.crypto)
    
    // Java 8+ Support
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    
    // Testing - Unit Tests
    testImplementation(libs.bundles.testing.unit)
    
    // Testing - Android Tests
    androidTestImplementation(libs.bundles.testing.android)
}

// Custom task to check for security vulnerabilities
tasks.register("securityCheck") {
    group = "verification"
    description = "Run security checks on dependencies"
    
    doLast {
        println("🔒 Running security checks...")
        println("Analyzing dependencies for known vulnerabilities...")
        // This would integrate with tools like OWASP Dependency Check
    }
}

// Task to generate build information
tasks.register("buildInfo") {
    group = "build"
    description = "Display build information"
    
    doLast {
        println("📱 QA Assist Build Information")
        println("═══════════════════════════════════════")
        println("App Version: ${android.defaultConfig.versionName}")
        println("Version Code: ${android.defaultConfig.versionCode}")
        println("Target SDK: ${android.defaultConfig.targetSdk}")
        println("Min SDK: ${android.defaultConfig.minSdk}")
        println("Build Type: ${gradle.startParameter.taskNames}")
        println("Build Time: ${LocalDateTime.now()}")
        println("═══════════════════════════════════════")
    }
}
