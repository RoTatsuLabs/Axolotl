@file:Suppress("DEPRECATION")

package com.rotatsulabs.axolotl.core.ui.pages.nowplaying

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.ripple
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderPositions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.insets.navigationBarsHeight
import kotlinx.coroutines.delay
import com.rotatsulabs.axolotl.core.playback.MediaController.mediaControl
import com.rotatsulabs.axolotl.core.playback.util.Vibrator
import com.rotatsulabs.axolotl.core.data.settings.SettingsLibrary
import com.rotatsulabs.axolotl.core.ui.pages.nowplaying.NowPlayingPage.Lyric
import com.rotatsulabs.axolotl.core.ui.pages.nowplaying.NowPlayingPage.PlayingList
import com.rotatsulabs.axolotl.core.ui.widgets.audio.MusicQualityIndicator
import com.rotatsulabs.axolotl.core.ui.widgets.basic.KeyedContent
import com.rotatsulabs.axolotl.core.ui.widgets.effects.overlayEffect
import com.rotatsulabs.axolotl.core.R

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PlayerControl(
    isPlayingLambda: () -> Boolean,
    isPlayingOnChanged: (Boolean) -> Unit,
    onPrevious: () -> Unit,
    onStatus: (Boolean) -> Unit,
    onNext: () -> Unit,
    onSeek: (Float) -> Unit,
    onLyrics: () -> Unit,
    onPlaylist: () -> Unit,
    nowPage: () -> String,
    onSlider: () -> Unit,
    onWhile: suspend () -> Unit,
    modifier: Modifier
) {
    val playingDuration = rememberSaveable(key = "PlayerControl_playingDuration") {
        mutableLongStateOf(0L)
    }
    val playingPosition = rememberSaveable(key = "PlayerControl_playingPosition") {
        mutableLongStateOf(0L)
    }
    val context = LocalContext.current
    val playedTime = rememberSaveable(key = "PlayerControl_playedTime") { mutableStateOf("0:00") }
    val remainingTime =
        rememberSaveable(key = "PlayerControl_remainingTime") { mutableStateOf("-0:00") }
    val sliderPosition = remember("PlayerControl_sliderPosition") { mutableFloatStateOf(0f) }
    val isSliding = remember("PlayerControl_isSliding") {
        mutableStateOf(false)
    }

    KeyedContent {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 25.dp)
                .padding(bottom = 15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            println("重组：控制区域内部")

            KeyedContent {
                // 启动作用
                KeyedContent {
                    val lifecycleState =
                        LocalLifecycleOwner.current.lifecycle.currentStateFlow.collectAsState()

                    LaunchedEffect(Unit) {
                        var lastPosition = 0L
                        while (true) {
                            //isPlaying.value = /*mediaControl?.isPlaying ?: false*/ FadeExo.targetStatus != 0
                            if (lifecycleState.value.isAtLeast(Lifecycle.State.RESUMED)) {
                                playingDuration.longValue = mediaControl?.duration ?: 0
                                playingPosition.longValue = mediaControl?.currentPosition ?: 0

                                if (!isSliding.value && playingDuration.longValue > 0L) {
                                    val totalSeconds =
                                        playingPosition.longValue.coerceAtLeast(0) / 1000
                                    if (totalSeconds != lastPosition) {
                                        playedTime.value = formatTime(totalSeconds)

                                        sliderPosition.floatValue =
                                            playingPosition.longValue.coerceAtLeast(0).toFloat()

                                        val remainingSeconds =
                                            playingDuration.longValue.coerceAtLeast(0) / 1000 - totalSeconds
                                        remainingTime.value = "-${formatTime(remainingSeconds)}"
                                        lastPosition = totalSeconds
                                    }
                                }

                                onWhile()
                            }

                            delay(700)
                        }
                    }
                }

                // 进度条
                KeyedContent {
                    //println("重组：控制区域内部 - 进度条")
                    Slider(
                        value = sliderPosition.floatValue,
                        onValueChange = { newValue ->
                            isSliding.value = true

                            sliderPosition.floatValue = newValue
                            val newTotalSeconds = newValue.toLong() / 1000
                            playedTime.value = formatTime(newTotalSeconds)

                            val newRemainingSeconds =
                                playingDuration.longValue / 1000 - newTotalSeconds
                            remainingTime.value = "-${formatTime(newRemainingSeconds)}"

                            onSlider()
                        },
                        onValueChangeFinished = {
                            Vibrator.longClick(context)
                            onSeek(sliderPosition.floatValue)
                            isSliding.value = false
                        },
                        valueRange = 0f..playingDuration.longValue.toFloat().coerceAtLeast(0f),
                        colors = SliderDefaults.colors(
                            activeTrackColor = Color.White,
                            inactiveTrackColor = Color(0x0DFFFFFF)
                        ),
                        modifier = Modifier
                            .overlayEffect()
                            .alpha(0.45f)
                            .height(14.dp),
                        thumb = {
                        },
                        track = {
                            Track(
                                sliderPositions = SliderPositions(
                                    initialActiveRange = 0f..(sliderPosition.floatValue / playingDuration.longValue)
                                ), height = 7.dp
                            )
                        }
                    )
                }

                // 控制按钮&进度文本
                KeyedContent {
                    //println("重组：控制区域内部 - 控制按钮&进度文本")
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp, horizontal = 7.dp)
                            .height(22.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = playedTime.value,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 0.3.sp,
                                color = Color.White.copy(alpha = 0.3f),
                                modifier = Modifier.overlayEffect()
                            )
                            Text(
                                text = remainingTime.value,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 0.3.sp,
                                color = Color.White.copy(alpha = 0.3f),
                                modifier = Modifier.overlayEffect()
                            )
                        }

                        MusicQualityIndicator()
                    }


                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(61.dp)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = ripple(bounded = false),
                                        onClick = {
                                            Vibrator.click(context)
                                            onPrevious()
                                        }),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painterResource(id = R.drawable.ic_nowplaying_rewind),
                                    contentDescription = "Previous",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(10.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(43.dp))

                            Box(
                                modifier = Modifier
                                    .size(58.5.dp)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = ripple(bounded = false),
                                        onClick = {
                                            Vibrator.click(context)
                                            isPlayingOnChanged(!isPlayingLambda())
                                            onStatus(isPlayingLambda())
                                        }),
                                contentAlignment = Alignment.Center
                            ) {
                                AnimatedContent(targetState = isPlayingLambda(), transitionSpec = {
                                    (scaleIn(initialScale = 0.3f) + fadeIn()).togetherWith(
                                        scaleOut(
                                            targetScale = 0.3f
                                        ) + fadeOut()
                                    )
                                }) {
                                    if (it) {
                                        Icon(
                                            painterResource(id = R.drawable.ic_nowplaying_pause),
                                            contentDescription = "Pause",
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(10.dp)
                                        )
                                    } else {
                                        Icon(
                                            painterResource(id = R.drawable.ic_nowplaying_play),
                                            contentDescription = "Play",
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(9.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.width(43.dp))
                            Box(
                                modifier = Modifier
                                    .size(61.dp)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = ripple(bounded = false),
                                        onClick = {
                                            Vibrator.click(context)
                                            onNext()
                                        }),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painterResource(id = R.drawable.ic_nowplaying_fforward),
                                    contentDescription = "Next",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(10.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 音量调节
            KeyedContent {
                if (SettingsLibrary.NowPlayingShowVolumeBar) {
                    VolumeSlider(context = context, onSlider)
                }
            }

            // 底部 歌词&播放列表
            KeyedContent {
                //println("重组：控制区域内部 - 底部栏")
                Row(
                    modifier = Modifier
                        .overlayEffect()
                        .fillMaxWidth()
                        .alpha(0.4f),
                    horizontalArrangement = Arrangement.Center
                ) {
                    val dp = 32.dp
                    Box(
                        modifier = Modifier
                            .height(36.dp)
                            .weight(1f)
                            .clickable(
                                onClick = { onLyrics() },
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }),
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedContent(
                            targetState = nowPage() == Lyric,
                            transitionSpec = {
                                fadeIn() togetherWith fadeOut()
                            }) {
                            if (it) {
                                Icon(
                                    painterResource(id = R.drawable.ic_nowplaying_lyricson),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(dp)
                                )
                            } else {
                                Icon(
                                    painterResource(id = R.drawable.ic_nowplaying_lyrics),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(0.1f))

                    AirPlay()

                    Spacer(modifier = Modifier.weight(0.1f))

                    Box(
                        modifier = Modifier
                            .height(36.dp)
                            .weight(1f)
                            .clickable(
                                onClick = { onPlaylist() },
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }),
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedContent(
                            targetState = nowPage() == PlayingList,
                            transitionSpec = {
                                fadeIn() togetherWith fadeOut()
                            }) {
                            if (it) {
                                Icon(
                                    painterResource(id = R.drawable.ic_nowplaying_queueon),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(dp)
                                )
                            } else {
                                Icon(
                                    painterResource(id = R.drawable.ic_nowplaying_queue),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(dp)
                                )
                            }
                        }
                    }
                }
            }

            // 边距填充
            /*KeyedContent {
                Spacer(modifier = Modifier.navigationBarsHeight(5.dp))
            }*/
            // 为显示设备名称，迁移到 AirPlay 底部处理
        }
    }
}
