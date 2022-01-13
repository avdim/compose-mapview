package com.map

import java.io.File

actual fun isFileExists(path:String):Boolean =
    File(path).exists()

actual fun getFileSeparator():String=
    File.separator
