#!/bin/bash
set -euo pipefail
shopt -s inherit_errexit

./gradlew :include-model:model:test
./gradlew android:assembleDebug
./gradlew browser:jsBrowserWebpack
./gradlew desktop:run
