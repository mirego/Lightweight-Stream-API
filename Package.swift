// swift-tools-version:5.9
//
// GENERATED from Package.swift.template by the `spmPackage` Gradle task — do not
// edit by hand. The dependency version comes from gradle.properties (single source).
//
import PackageDescription

// Lightweight-Stream-API — J2ObjC library, distributed for Swift Package Manager.
// Generated Obj-C (MRC) pre-compiled into LightweightStreamApi.xcframework. Unlike
// the flat-header libs, this one keeps package directories (com/annimon/stream/...);
// the headers ship structured and consumers include them by package path via
// J2ObjC header-mapping. Depends only on the J2ObjC runtime (objects resolved once
// at the consumer's final link).
let package = Package(
    name: "LightweightStreamApi",
    platforms: [.iOS(.v12)],
    products: [
        .library(name: "LightweightStreamApi", targets: ["LightweightStreamApi"])
    ],
    dependencies: [
        .package(url: "https://github.com/mirego/j2objc.git", from: "3.1.0")
    ],
    targets: [
        // Committed at the release tag (private repo → consumed via authenticated git clone).
        .binaryTarget(
            name: "LightweightStreamApiObjC",
            path: "LightweightStreamApi.xcframework"
        ),
        .target(
            name: "LightweightStreamApi",
            dependencies: [
                "LightweightStreamApiObjC",
                .product(name: "J2ObjCRuntime", package: "j2objc")
            ]
        )
    ]
)
