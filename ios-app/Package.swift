// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "InspectionApp",
    platforms: [
        .iOS("17.0")
    ],
    products: [
        .iOSApplication(
            name: "InspectionApp",
            targets: ["InspectionApp"],
            bundleIdentifier: "com.qaassist.InspectionApp",
            teamIdentifier: "",
            displayVersion: "1.0",
            bundleVersion: "1",
            appIcon: .placeholder(icon: .clipboard),
            accentColor: .presetColor(.blue),
            supportedDeviceFamilies: [
                .pad,
                .phone
            ],
            supportedInterfaceOrientations: [
                .portrait,
                .landscapeRight,
                .landscapeLeft,
                .portraitUpsideDown(.when(deviceFamilies: [.pad]))
            ],
            capabilities: [
                .camera(purposeString: "Used to capture photos for inspections"),
                .photoLibrary(purposeString: "Used to select photos from your library"),
                .locationWhenInUse(purposeString: "Used to record inspection location")
            ]
        )
    ],
    dependencies: [
        .package(url: "https://github.com/3973770/SwiftXLSX", from: "0.1.0")
    ],
    targets: [
        .executableTarget(
            name: "InspectionApp",
            dependencies: [
                .product(name: "SwiftXLSX", package: "SwiftXLSX")
            ],
            path: "Sources"
        )
    ]
)
