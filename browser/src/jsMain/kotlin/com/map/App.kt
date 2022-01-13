package com.map

import kotlinx.browser.document
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.*
import kotlin.random.Random
import kotlin.reflect.KClass

fun main() {
    println("Hello Compose Web")
    ComposeCounterApp("root")
    val canvas = document.getElementById("map-canvas") as HTMLCanvasElement
    val ctx = canvas.getContext("2d") as CanvasRenderingContext2D
    ctx.fillStyle = "green";
    ctx.fillRect(0.0, 0.0, 1000.0, 1000.0)
    val img = Image() // Create new img element
    img.onload = {
        ctx.drawImage(image = img, dx = -50.0, dy = 10.0, dw = 100.0, dh = 50.0)
    }
    img.src = composeImg
}

fun <T : Any> Document.createElement(localName: String, kClass: KClass<T>): T = createElement(localName) as T //todo inline
fun Document.createCanvas(style: String) =
    (createElement("canvas", HTMLCanvasElement::class)).apply { setAttribute("style", style) }

fun ComposeCounterApp(rootId: String) {
    val composition = renderComposable(rootElementId = rootId) {
        MapView()
    }
    if (false) {
        composition.dispose()
    }
}
