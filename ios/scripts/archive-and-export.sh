#!/usr/bin/env bash
# Archive and export Saúde Pilates without the Xcode GitHub / Xcode Cloud modal.
# Upload the resulting .ipa with the Transporter app (Mac App Store).

set -euo pipefail

cd "$(dirname "$0")/.."

echo "→ Generating Xcode project..."
xcodegen generate

ARCHIVE_PATH="build/SaudePilates.xcarchive"
EXPORT_PATH="build/export"

echo "→ Archiving (Release)..."
xcodebuild -scheme SaudePilates \
  -destination 'generic/platform=iOS' \
  -configuration Release \
  -archivePath "$ARCHIVE_PATH" \
  archive

echo "→ Exporting App Store .ipa..."
rm -rf "$EXPORT_PATH"
xcodebuild -exportArchive \
  -archivePath "$ARCHIVE_PATH" \
  -exportPath "$EXPORT_PATH" \
  -exportOptionsPlist ExportOptions.plist \
  -allowProvisioningUpdates

IPA="$EXPORT_PATH/SaudePilates.ipa"
echo ""
echo "✓ Done: $IPA"
echo ""
echo "Next: open Transporter (Mac App Store), sign in, drag this .ipa, click Deliver."
