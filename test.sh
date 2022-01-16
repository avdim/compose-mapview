#!/bin/bash
set -euo pipefail
#shopt -s inherit_errexit

./gradlew unit-tests:test
./gradlew android:assembleDebug
./gradlew browser:jsBrowserWebpack
./gradlew desktop:run
