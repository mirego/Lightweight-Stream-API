#!/usr/bin/env bash
#
# Build this library's xcframework and publish its SPM SemVer tag. Invoked by the
# `spmPublish` Gradle task during `release` (after the J2ObjC transpile in the
# release check tasks). Generic across the J2ObjC libs (private repos → the
# xcframework is committed at the tag and consumed via .binaryTarget(path:)).
#
# The xcframework is large and is NOT kept on the branch: we attach it to a commit
# made with `git commit-tree` (which does NOT move HEAD/the branch) and push only
# the SemVer TAG. So `master` stays clean while the tag carries the binary — even
# though the surrounding `release` flow pushes the branch afterwards.
#
# Env:
#   SPM_VERSION  SemVer tag to publish (e.g. 1.521.0)  (required)
#   PUBLISH      1 = build + tag + push; 0 = build-only dry run (default: 0)
set -euo pipefail
cd "$(dirname "$0")/.."

SPM_VERSION="${SPM_VERSION:?set SPM_VERSION}"
PUBLISH="${PUBLISH:-0}"

echo "==> Building xcframework"
./scripts/build-xcframework.sh

if [ "${PUBLISH}" != "1" ]; then
  echo "==> PUBLISH=0 (dry run): built only, not tagging."
  exit 0
fi

echo "==> Tagging ${SPM_VERSION} with the xcframework (without moving the branch)"
git add -f ./*.xcframework
tree="$(git write-tree)"
commit="$(git -c user.name="GitHub Actions" -c user.email="actions@github.com" \
            commit-tree "${tree}" -p HEAD -m "Swift Package Manager xcframework ${SPM_VERSION}")"
git read-tree HEAD            # restore the index; HEAD/branch are untouched
git tag -f "${SPM_VERSION}" "${commit}"
git push -f origin "${SPM_VERSION}"   # tag-only push; the binary rides along with the tag
echo "==> Published ${SPM_VERSION} (master left clean)"
