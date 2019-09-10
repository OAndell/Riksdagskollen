for file in fastlane/metadata/android/sv-SE/images/phoneScreenshots/*.png
do
  mv "$file" "${file/_*.png/.png}"
done