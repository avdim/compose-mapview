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
