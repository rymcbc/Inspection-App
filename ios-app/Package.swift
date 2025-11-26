// swift-tools-version: 5.9
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "InspectionApp",
    platforms: [
        .iOS(.v17),
        .macOS(.v14)
    ],
    products: [
        .executable(
            name: "InspectionApp",
            targets: ["InspectionApp"]
        )
    ],
    dependencies: [
        .package(url: "https://github.com/3973770/SwiftXLSX.git", from: "0.1.0")
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
