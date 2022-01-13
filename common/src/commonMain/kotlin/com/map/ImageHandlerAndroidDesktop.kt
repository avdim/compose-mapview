/*
 * Copyright 2020-2021 JetBrains s.r.o. and respective authors and developers.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE.txt file.
 */

package com.map

fun getNameURL(url: String): String {
    return url.substring(url.lastIndexOf('/') + 1, url.length)
}

private fun todoCache() {
    val cachePath: String? = null
    //todo cache
    // for android val directory = context.cacheDir.absolutePath
    // for desktop val cacheImagePath = System.getProperty("user.home")!! + File.separator + "Pictures/mapview" + File.separator //todo tmp

//    for (source in list) {
//        val name = getNameURL(source)
//        val path = cachePath + getFileSeparator() + name
//
//        if (isFileExists(path + "info")) {
//            addCachedMiniature(filePath = path, outList = result)
//        } else {
//            addFreshMiniature(source = source, outList = result, path = cachePath)
//        }
//
//        result.last().id = result.size - 1
//    }
//
    fun addFreshMiniature(
        source: String,
        outList: MutableList<Picture>,
        path: String?
    ) {
        val scaledPicture: Picture = loadFullImage(source).scale(200, 200)
        outList.add(scaledPicture)
        cacheImage(path + getNameURL(source), scaledPicture)
    }

    fun addCachedMiniature(
        filePath: String,
        outList: MutableList<Picture>
    ) {
        try {
//            val info = readPictureInfoFromFile(filePath + cacheImagePostfix)
//            val result: AbstractImageData = readAbstractImageDataFromFile(filePath)
//            val picture = Picture(
//                info.source,
//                getNameURL(info.source),
//                result,
//                info.width,
//                info.height
//            )
//            outList.add(picture)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
