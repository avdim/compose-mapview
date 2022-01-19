package com.map

import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.Document
import org.w3c.dom.HTMLCanvasElement
import kotlin.reflect.KClass

fun main() {
    ComposeCounterApp("root")
}

fun <T : Any> Document.createElement(localName: String, kClass: KClass<T>): T = createElement(localName) as T
fun Document.createCanvas(style: String) =
    (createElement("canvas", HTMLCanvasElement::class)).apply { setAttribute("style", style) }

fun ComposeCounterApp(rootId: String) {
    val composition = renderComposable(rootElementId = rootId) {
        MapView(
            modifier = size(1000, 800),
            mapTilerSecretKey = MAPTILER_SECRET_KEY,
//            latitude = 59.999394,
//            longitude = 29.745412,
//            startScale = 840.0,
        )

    }
    if (false) {
        // todo для JS надо предоставлять наружу callback для завершения работы MapView
        composition.dispose()
    }
}
