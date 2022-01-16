package com.map

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import kotlinx.coroutines.launch
import java.awt.Color
import java.awt.Font
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import javax.imageio.ImageIO

fun createDownloadImageRepository(): ImageRepository =
    if (USE_FAKE_REPOSITORY_ON_DEKSTOP) {
        createFakeRepository()
    } else {
        createRealRepository()
    }

private fun createRealRepository() = object : ImageRepository {
    val ktorClient: HttpClient = HttpClient(CIO)

    override suspend fun getImage(tile: Tile): Picture {
        val byteArray = ktorClient.get<ByteArray>(tile.tileUrl)
        return Picture(
            image = byteArray
        )
    }
}

private fun createFakeRepository() = object : ImageRepository {
    override suspend fun getImage(tile: Tile): Picture {
        val byteArray = mkBitmap(tile.zoom, tile.x, tile.y)
        return Picture(
            image = byteArray
        )
        //BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
    }
}

@Synchronized
fun mkBitmap(z: Int, x: Int, y: Int): ByteArray {
    val width = 512
    val height = 512

    // TYPE_INT_ARGB specifies the image format: 8-bit RGBA packed
    // into integer pixels
    val bi = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    val ig2 = bi.createGraphics()
    val font = Font("TimesRoman", Font.BOLD, 20)
    ig2.font = font
    val message = "$z  \n ($x, $y)"
    val fontMetrics = ig2.fontMetrics
    val stringWidth = fontMetrics.stringWidth(message)
    val stringHeight = fontMetrics.ascent
    ig2.paint = Color.black
    ig2.drawString(message, (width - stringWidth) / 2, height / 2 + stringHeight / 4)
    ig2.drawRect(1, 1, 510, 510)
    ig2.drawOval(3, 3, 3, 3)
    ig2.drawOval(512 - 3, 3, 3, 3)
    ig2.drawOval(3, 512 - 3, 3, 3)
    ig2.drawOval(512 - 3, 512 - 3, 3, 3)

    val tempFile = File("/dev/shm/temp.png")
    if (!tempFile.exists()) {
        tempFile.createNewFile()
    }
    ImageIO.write(bi, "PNG", tempFile)
    tempFile.readBytes()
    return tempFile.readBytes()
}

fun decorateWithInMemoryCache(imageRepository: ImageRepository): ImageRepository = object : ImageRepository {
    val cache: MutableMap<Tile, Picture> = ConcurrentHashMap()//todo LRU cache как в video Тагира Валеева LinkedHashMap
    override suspend fun getImage(tile: Tile): Picture {
        //todo вставать в блокировку по ключу или вешать обработчики на ожидание по ключу как в видео Романа Елизарова, actor
        val fromCache = cache[tile]
        if (fromCache != null) {
            return fromCache
        }
        val result = imageRepository.getImage(tile)
        cache[tile] = result
        return result
    }
}

fun decorateWithDiskCache(imageRepository: ImageRepository): ImageRepository = object : ImageRepository {

    val cacheDir: File? //todo переделать на nio.Path для неблокирующих операций
    //val cacheDir = System.getProperty("user.home")!! + File.separator + "map-view-cache" + File.separator

    init {
        // Для HOME директории MacOS требует разрешения.
        // Чтобы не просить разрешений созданим кэш во временной директории.
        val tmpDirStr = System.getProperty("java.io.tmpdir")
        val fakeStr =
            if (USE_FAKE_REPOSITORY_ON_DEKSTOP) "-fake" else "" // Если работает с фальшивими данными, созданим дургую директорию
        val resultCacheDir = File(tmpDirStr).resolve(CACHE_DIR_NAME + fakeStr)
        cacheDir = try {
            resultCacheDir.mkdirs()
            resultCacheDir
        } catch (t: Throwable) {
            t.printStackTrace()
            println("Can't create cache dir $resultCacheDir")
            println("Will work without disk cache")
            null
        }
    }

    override suspend fun getImage(tile: Tile): Picture {
        if (cacheDir == null) {
            return imageRepository.getImage(tile)
        }
        val file = with(tile) {
            cacheDir.resolve("tile-$zoom-$x-$y.png")
        }
        //todo вставать в synchronized блокировку по ключу tile
        val bytes: ByteArray? =
            if (file.exists()) {
                try {
                    file.readBytes()
                } catch (t: Throwable) {
                    t.printStackTrace()
                    println("Can't read file $file")
                    println("Will work without disk cache")
                    null
                }
            } else {
                null
            }
        if (bytes == null) {
            val image = imageRepository.getImage(tile)
            getBackgroundScope().launch {
                // save image
                try {
                    file.writeBytes(image.image)
                } catch (t: Throwable) {
                    println("Can't save image to file $file")
                    println("Will work without disk cache")
                }
            }
            return image
        }
        return Picture(
            bytes
        )
    }

    private fun todoCache() {
        val cachePath: String? = null
        // for desktop val cacheImagePath = System.getProperty("user.home")!! + File.separator + "Pictures/mapview" + File.separator

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
    }

    fun addCachedMiniature(filePath: String, outList: MutableList<Picture>) {
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
    }

    fun cacheImage(path: String, picture: Picture) {
        try {
            File(path).writeBytes(picture.image)
//        ImageIO.write(picture.image, "png", File(path))
//
//        val bw =
//            BufferedWriter(
//                OutputStreamWriter(
//                    FileOutputStream(path + cacheImagePostfix),
//                    StandardCharsets.UTF_8
//                )
//            )
//
//        bw.write(picture.url)
//        bw.write("\r\n${picture.width}")
//        bw.write("\r\n${picture.height}")
//        bw.close()

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
