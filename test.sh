#!/bin/bash
set -euo pipefail
#shopt -s inherit_errexit

./gradlew unit-tests:test unit-tests:check
./gradlew sample-android:assembleDebug
./gradlew sample-browser:jsBrowserWebpack
./gradlew sample-desktop:run
