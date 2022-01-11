/*
 * Copyright 2020-2021 JetBrains s.r.o. and respective authors and developers.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE.txt file.
 */

package example.imageviewer.model

data class Picture(
    var source: String = "",
    var name: String = "",
    var image: AbstractImageData,
    var width: Int = 0,
    var height: Int = 0,
    var id: Int = 0
)

expect class AbstractImageData

data class PictureInfo(
    val source: String,
    val width: Int,
    val height: Int
)

expect fun PictureInfo.saveToFile(path: String)
expect fun readPictureInfoFromFile(path: String): PictureInfo
expect fun readAbstractImageDataFromFile(path: String): AbstractImageData
