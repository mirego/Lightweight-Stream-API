#!/usr/bin/env bash
#
# Build LightweightStreamApi.xcframework from the J2ObjC-generated Obj-C (MRC only
# — no hand-written iOS layer). Depends only on the J2ObjC runtime headers at
# COMPILE time (their objects are NOT embedded; symbols resolve at the consumer's
# final link via the runtime's own SPM package).
#
# NOTE: unlike scratch/itch (which generate flat headers via
# --no-package-directories), this lib KEEPS package directories
# (com/annimon/stream/...) and its sources #include via those package paths
# (e.g. #include "com/annimon/stream/Optional.h"). So we stage headers preserving
# the tree and put the tree root on the include path. Consumers (e.g. canadiens
# core) reference the same package paths via J2ObjC header-mapping, so the
# xcframework must ship the structured headers. NO module map (textual includes;
# an umbrella map would break the J2ObjC INCLUDE_ALL/RESTRICT guards — see
# spm-no-module-maps).
#
# Slices: ios-arm64_arm64e (device) + ios-arm64 (simulator, Apple Silicon).
set -euo pipefail

cd "$(dirname "$0")/.."
ROOT="$(pwd)"

NAME="LightweightStreamApi"
MIN_IOS="12.0"
BUILD="${ROOT}/build-xcframework"
OUT="${ROOT}/${NAME}.xcframework"

GENERATED_DIRS=("${ROOT}/stream-j2objc")     # MRC, package-structured
ARC_DIRS=()                                   # none

# Upstream headers needed to COMPILE (not embed): the J2ObjC runtime only.
RUNTIME_INC="${RUNTIME_INC:-$HOME/.j2objc/j2objc-3.1-mirego/include}"
JSR305_INC="${JSR305_INC:-$HOME/.j2objc/j2objc-3.1-mirego/frameworks/JSR305.xcframework/ios-arm64_arm64e/Headers}"
UPSTREAM_INCS=("${RUNTIME_INC}" "${JSR305_INC}")

MRC_FLAGS=(-fno-objc-arc -fobjc-weak
    -Wno-nullability-completeness -Wno-deprecated-declarations
    -Wno-strict-prototypes -Wno-documentation -Wno-unused-variable
    -Wno-unused-but-set-variable -Wno-unused-label -Wno-dangling-else
    -Wno-conditional-uninitialized)
ARC_FLAGS=(-fobjc-arc -fobjc-arc-exceptions -fobjc-weak
    -Wno-nullability-completeness -Wno-deprecated-declarations)

INC_FLAGS=(-I"${BUILD}/Headers")
for d in "${UPSTREAM_INCS[@]}"; do INC_FLAGS+=(-I"${d}"); done

rm -rf "${BUILD}" "${OUT}"
mkdir -p "${BUILD}/Headers"

echo "Staging headers (preserving package dirs)..."
for d in ${GENERATED_DIRS[@]+"${GENERATED_DIRS[@]}"}; do cp -R "${d}/." "${BUILD}/Headers/"; done
find "${BUILD}/Headers" -name '*.m' -delete
echo "  $(find "${BUILD}/Headers" -name '*.h' | wc -l | tr -d ' ') headers"

GEN_SRCS=(); for d in ${GENERATED_DIRS[@]+"${GENERATED_DIRS[@]}"}; do while IFS= read -r f; do GEN_SRCS+=("$f"); done < <(find "${d}" -name '*.m'); done
ARC_SRCS=(); for d in ${ARC_DIRS[@]+"${ARC_DIRS[@]}"}; do while IFS= read -r f; do ARC_SRCS+=("$f"); done < <(find "${d}" -name '*.m'); done

compile_group () {
    local objdir="$1" sdkpath="$2" mintarget="$3"; shift 3
    mkdir -p "${objdir}"
    ( cd "${objdir}" && xcrun --sdk "${SDK}" clang -c "$@" -isysroot "${sdkpath}" "${mintarget}" "${INC_FLAGS[@]}" )
}

build_slice () {
    local slice="$1"; SDK="$2"; shift 2
    local archs=("$@")
    local sdkpath; sdkpath="$(xcrun --sdk "${SDK}" --show-sdk-path)"
    local objdir="${BUILD}/${slice}"
    local archflags=(); for a in "${archs[@]}"; do archflags+=(-arch "${a}"); done
    local mintarget
    if [ "${SDK}" = "iphonesimulator" ]; then mintarget="-mios-simulator-version-min=${MIN_IOS}"; else mintarget="-miphoneos-version-min=${MIN_IOS}"; fi

    local libs=()
    if [ "${#GEN_SRCS[@]}" -gt 0 ]; then
        echo "  compiling generated (MRC)..."
        compile_group "${objdir}/gen" "${sdkpath}" "${mintarget}" "${archflags[@]}" "${MRC_FLAGS[@]}" "${GEN_SRCS[@]}"
        libs+=("${objdir}"/gen/*.o)
    fi
    if [ "${#ARC_SRCS[@]}" -gt 0 ]; then
        echo "  compiling iOS layer (ARC)..."
        compile_group "${objdir}/arc" "${sdkpath}" "${mintarget}" "${archflags[@]}" "${ARC_FLAGS[@]}" "${ARC_SRCS[@]}"
        libs+=("${objdir}"/arc/*.o)
    fi
    xcrun libtool -static -o "${BUILD}/lib${NAME}-${slice}.a" "${libs[@]}"
    echo "  ${slice}: $(lipo -archs "${BUILD}/lib${NAME}-${slice}.a")"
}

echo "Building device slice..."
build_slice "device" "iphoneos" arm64 arm64e
echo "Building simulator slice..."
build_slice "sim" "iphonesimulator" arm64

echo "Creating xcframework..."
xcodebuild -create-xcframework \
    -library "${BUILD}/lib${NAME}-device.a" -headers "${BUILD}/Headers" \
    -library "${BUILD}/lib${NAME}-sim.a" -headers "${BUILD}/Headers" \
    -output "${OUT}"

echo "Done: ${OUT}"
plutil -p "${OUT}/Info.plist" | grep -E "LibraryIdentifier|SupportedPlatformVariant" || true
