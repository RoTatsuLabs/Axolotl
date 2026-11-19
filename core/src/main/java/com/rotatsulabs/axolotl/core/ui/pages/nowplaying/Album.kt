@file:Suppress("DEPRECATION")

package com.rotatsulabs.axolotl.core.ui.pages.nowplaying

import android.net.Uri
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.rotatsulabs.axolotl.core.ui.pages.nowplaying.NowPlayingPage.Album
import com.rotatsulabs.axolotl.core.ui.widgets.basic.ImageQuality
import com.rotatsulabs.axolotl.core.ui.widgets.basic.ShadowImageWithCache
import com.rotatsulabs.axolotl.core.ui.widgets.basic.KeyedContent

@Composable
internal fun ColumnScope.Album(
    modifier: Modifier,
    albumUrl: () -> Uri?,
    isPlaying: () -> Boolean
) = Box(
    Modifier
        .weight(1f)
        .padding(top = 20.dp)
        .padding(horizontal = 15.dp)
        .padding(bottom = 33.dp),
    contentAlignment = Alignment.BottomCenter
) {
    val springSpec: AnimationSpec<Float> = remember("Album_springSpec") {
        SpringSpec(stiffness = 300f, dampingRatio = 1f, visibilityThreshold = 0.001f)
    }

    val tweenSpec: AnimationSpec<Float> = remember("Album_tweenSpec") {
        TweenSpec(durationMillis = 350, easing = EaseOutQuart)
    }

    val scale = animateFloatAsState(
        targetValue = if (isPlaying()) 0f else 1f,
        animationSpec = if (isPlaying()) springSpec else tweenSpec,
        visibilityThreshold = 0.001f
    )

    KeyedContent {
        val dp = (7 + (27 * scale.value)).dp
        ShadowImageWithCache(
            dataLambda = albumUrl, contentDescription = null, modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    compositingStrategy = CompositingStrategy.ModulateAlpha
                    // scaleX = scale.value
                    // scaleY = scale.value
                }
                .padding(start = dp, end = dp, bottom = dp)
                .then(modifier),
            imageQuality = ImageQuality.RAW,
            shadowOverlay = true
        )
    }
}
