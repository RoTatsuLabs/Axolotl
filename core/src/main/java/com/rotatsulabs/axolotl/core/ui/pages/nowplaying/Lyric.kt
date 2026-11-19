@file:Suppress("DEPRECATION")

package com.rotatsulabs.axolotl.core.ui.pages.nowplaying

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.statusBarsHeight
import com.rotatsulabs.axolotl.core.playback.MediaController.mediaControl
import com.rotatsulabs.axolotl.core.playback.lyrics.LyricMediaEvent
import com.rotatsulabs.axolotl.core.playback.lyrics.LyricUiConfig
import com.rotatsulabs.axolotl.core.data.settings.SettingsLibrary
import com.rotatsulabs.axolotl.core.data.viewmodel.MainViewModel
import com.rotatsulabs.axolotl.core.data.viewmodel.MediaViewModel
import com.rotatsulabs.axolotl.core.ui.pages.nowplaying.NowPlayingPage.Lyric
import com.rotatsulabs.axolotl.core.ui.widgets.lyrics.LyricView
import com.rotatsulabs.axolotl.core.ui.widgets.basic.KeyedContent
import com.rotatsulabs.axolotl.core.R

@Composable
internal fun Lyric(
    lyricEntries: () -> List<List<Pair<Float, String>>>,
    weightLambda: () -> Boolean,
    translationLambda: () -> Boolean,
    mainViewModel: MainViewModel,
    mediaViewModel: MediaViewModel,
    onBackClick: () -> Unit
) = KeyedContent {

    val context = LocalContext.current

    println("重组：LyricView 外层 2")

    Column(
        Modifier
            .fillMaxSize()
    ) {
        KeyedContent {
            println("重组：LyricView 外层 1")

            Spacer(modifier = Modifier.statusBarsHeight(110.dp))


            LyricView(
                //mediaViewModel = mediaViewModel,
                lyricEntriesLambda = lyricEntries,
                liveTimeLambda = {
                    (mediaControl?.currentPosition ?: 0).toInt()
                },
                mediaEvent = object : LyricMediaEvent {
                    override fun onSeek(position: Int) {
                        mediaControl?.seekTo(position.toLong())
                    }
                },
                translationLambda = translationLambda,
                blurLambda = {
                    SettingsLibrary.LyricBlurEffect
                },
                uiConfig = LyricUiConfig(
                    noLyricsText = stringResource(id = R.string.tip_no_lyrics)
                ),
                weightLambda = weightLambda,
                modifier = Modifier.drawWithCache {
                    onDrawWithContent {
                        val overlayPaint = Paint().apply {
                            blendMode = BlendMode.Plus
                        }
                        val rect = Rect(0f, 0f, size.width, size.height)
                        val canvas = this.drawContext.canvas

                        canvas.saveLayer(rect, overlayPaint)

                        val colors = if (weightLambda()) {
                            listOf(
                                Color.Transparent,
                                Color(0x59000000),
                                Color.Black,
                                Color.Black,
                                Color.Black,
                                Color.Black,
                                Color.Black,
                                Color.Black,
                                Color(0x59000000),
                                Color(0x21000000),
                                Color.Transparent,
                                Color.Transparent,
                                Color.Transparent,
                                Color.Transparent,
                                Color.Transparent,
                                Color.Transparent,
                                Color.Transparent,
                                Color.Transparent
                            )
                        } else {
                            listOf(
                                Color.Transparent,
                                Color(0x59000000),
                                Color.Black,
                                Color.Black,
                                Color.Black,
                                Color.Black,
                                Color.Black,
                                Color.Black,
                                Color.Black,
                                Color.Black,
                                Color.Black,
                                Color.Black,
                                /*Color(0xD9000000),
                                Color(0xA6000000),
                                Color(0x73000000),
                                Color(0x59000000),
                                Color(0x3F000000),
                                Color(0x21000000),
                                Color(0x0C000000),*/
                                Color.Black,
                                Color.Black,
                                Color.Black,
                                Color.Black,
                                Color.Black,
                                Color.Black,
                                Color.Black
                            )
                        }

                        drawContent()

                        drawRect(
                            brush = Brush.verticalGradient(colors),
                            blendMode = BlendMode.DstIn
                        )

                        canvas.restore()
                    }
                },
                onBackClick = onBackClick
            )
        }
    }
}
