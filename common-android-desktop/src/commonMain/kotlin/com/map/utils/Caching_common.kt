/*
 * Copyright 2020-2021 JetBrains s.r.o. and respective authors and developers.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE.txt file.
 */

package com.map.utils

import com.map.model.Picture

val cacheImagePostfix = "info"
expect fun cacheImage(path: String, picture: Picture)

expect fun isFileExists(path:String):Boolean
expect fun getFileSeparator():String
