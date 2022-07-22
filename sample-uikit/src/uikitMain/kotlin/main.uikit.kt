/*
 * Copyright 2020-2022 JetBrains s.r.o. and respective authors and developers.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE.txt file.
 */

// Use `xcodegen` first, then `open ./ComposeMapView.xcodeproj` and then Run button in XCode.
import androidx.compose.foundation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Application
import kotlinx.cinterop.*
import platform.UIKit.*
import platform.Foundation.*
import com.map.*

fun main() {
    val args = emptyArray<String>()
    memScoped {
        val argc = args.size + 1
        val argv = (arrayOf("skikoApp") + args).map { it.cstr.ptr }.toCValues()
        autoreleasepool {
            UIApplicationMain(argc, argv, null, NSStringFromClass(SkikoAppDelegate))
        }
    }
}

class SkikoAppDelegate : UIResponder, UIApplicationDelegateProtocol {
    companion object : UIResponderMeta(), UIApplicationDelegateProtocolMeta

    @ObjCObjectBase.OverrideInit
    constructor() : super()

    private var _window: UIWindow? = null
    override fun window() = _window
    override fun setWindow(window: UIWindow?) {
        _window = window
    }

    override fun application(application: UIApplication, didFinishLaunchingWithOptions: Map<Any?, *>?): Boolean {
        window = UIWindow(frame = UIScreen.mainScreen.bounds)
        window!!.rootViewController = Application("MapView") {
            AnimatedMapView()
        }
        window!!.makeKeyAndVisible()
        return true
    }
}

@Composable
fun AnimatedMapView() {
    val infiniteTransition = rememberInfiniteTransition()
    val animatedScale: Float by infiniteTransition.animateFloat(
        initialValue = 2f,
        targetValue = 4200f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 5_000
                3.5f at 500
                100f at 2000
                4100f at 4_500
            },
            repeatMode = RepeatMode.Reverse
        )
    )
    val animatedMapState = derivedStateOf {
        MapState(
            latitude = 59.999394,
            longitude = 29.745412,
            scale = animatedScale.toDouble()
        )
    }
    MapView(
        modifier = Modifier.fillMaxSize(),
        state = animatedMapState,
        onStateChange = {}
    )
}
