@file:Suppress("DEPRECATION")

package com.rotatsulabs.axolotl.core.ui.pages.nowplaying

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.Player.REPEAT_MODE_OFF
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.blankj.utilcode.util.TimeUtils
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.rotatsulabs.axolotl.core.playback.MediaController.mediaControl
import com.rotatsulabs.axolotl.core.playback.MediaController.musicPlaying
import com.rotatsulabs.axolotl.core.playback.util.Vibrator
import com.rotatsulabs.axolotl.core.playback.player.FadeExo.fadePause
import com.rotatsulabs.axolotl.core.playback.player.FadeExo.fadePlay
import com.rotatsulabs.axolotl.core.data.settings.SettingsLibrary
import com.rotatsulabs.axolotl.core.data.model.artistsName
import com.rotatsulabs.axolotl.core.data.model.defaultArtistsName
import com.rotatsulabs.axolotl.core.data.model.defaultTitle
import com.rotatsulabs.axolotl.core.data.viewmodel.MainViewModel
import com.rotatsulabs.axolotl.core.data.viewmodel.MediaViewModel
import com.rotatsulabs.axolotl.core.data.state.MediaViewModelObject
import com.rotatsulabs.axolotl.core.ui.pages.nowplaying.NowPlayingPage.Album
import com.rotatsulabs.axolotl.core.ui.pages.nowplaying.NowPlayingPage.Lyric
import com.rotatsulabs.axolotl.core.ui.pages.nowplaying.NowPlayingPage.PlayingList
import com.rotatsulabs.axolotl.core.ui.widgets.lyrics.LyricView
import com.rotatsulabs.axolotl.core.ui.widgets.effects.FloatingLight
import com.rotatsulabs.axolotl.core.ui.widgets.basic.KeyedContent
import com.rotatsulabs.axolotl.core.ui.widgets.effects.overlayEffect
import com.rotatsulabs.axolotl.core.R

@Stable
object NowPlayingPage {
    const val Album = "Album"
    const val PlayingList = "PlayingList"
    const val Lyric = "Lyric"
}

private const val ShareAlbumKey = "album"
private const val AnimDurationMillis = 300

/*
private val MaterialFadeInTransitionSpec
    get() = SharedElementsTransitionSpec(
        pathMotionFactory = LinearMotionFactory,
        durationMillis = AnimDurationMillis,
        fadeMode = FadeMode.In,
        easing = EaseOutQuart
    )

private val MaterialFadeOutTransitionSpec
    get() = SharedElementsTransitionSpec(
        pathMotionFactory = LinearMotionFactory,
        durationMillis = AnimDurationMillis,
        fadeMode = FadeMode.Out,
        easing = EaseOutQuart
    )
*/

@ExperimentalSharedTransitionApi
@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun NowPlaying(
    mainViewModel: MainViewModel,
    mediaViewModel: MediaViewModel,
    navController: NavController,
    isPlayingStatusLambda: () -> Boolean,
    isPlayingOnChanged: (Boolean) -> Unit,
    nowPageLambda: () -> String,
    showMiniPlayer: () -> Boolean,
    nowPageOnChanged: (String) -> Unit
) =
    Surface(
        modifier = Modifier.fillMaxSize(),
        contentColor = Color.White,
        color = Color.Transparent
    ) {
        val context = LocalContext.current

        val lyricEntries: MutableState<List<List<Pair<Float, String>>>> =
            MediaViewModelObject.lyricEntries
        val bitmap: MutableState<Uri?> = MediaViewModelObject.bitmap

        val thisMusicPlaying = remember("NowPlaying_thisMusicPlaying") {
            musicPlaying
        }

        val lastClickTime = rememberSaveable(key = "NowPlaying_lastClickTime") {
            mutableLongStateOf(0L)
        }

        val showControl = rememberSaveable(key = "NowPlaying_showControl") {
            mutableStateOf(true)
        }

        val translation = rememberSaveable(key = "NowPlaying_translation") {
            mutableStateOf(SettingsLibrary.NowPlayingTranslation)
        }

        val shuffleModeEnabled = rememberSaveable(key = "NowPlaying_shuffleModeEnabled") {
            mutableStateOf(mediaControl?.shuffleModeEnabled ?: false)
        }
        val repeatMode = rememberSaveable(key = "NowPlaying_repeatMode") {
            mutableIntStateOf(mediaControl?.repeatMode ?: REPEAT_MODE_OFF)
        }

        /*val nowPage = rememberSaveable(key = "NowPlaying_nowPage") {
            MainViewModelObject.nowPage
        }*/

        println("重组：NowPlaying")

        // 触摸超时
        KeyedContent {
            LaunchedEffect(showControl.value, nowPageLambda(), lastClickTime.longValue) {
                if (nowPageLambda() != Lyric && !showControl.value) {
                    showControl.value = true
                }
                if (showControl.value) {
                    val time = 2500L
                    delay(time)
                    withContext(Dispatchers.Main) {
                        if (TimeUtils.getNowMills() - lastClickTime.longValue >= time && nowPageLambda() == Lyric) {
                            showControl.value = false
                        }
                    }
                }
            }
        }


        // 背景流光
        KeyedContent {
            /*BlendBackgroundView(
        bitmapLambda = { bitmap.value },
        isPlayingLambda = { isPlaying.value },
        nowPage = { nowPage.value }
    )*/
            println("重组：背景")

            FloatingLight(
                album = { bitmap.value },
                isPlaying = isPlayingStatusLambda,
                modifier = Modifier.fillMaxSize(),
                nowPage = { nowPageLambda() },
                showMiniPlayer = showMiniPlayer
            )
        }


        // 实际显示区
        KeyedContent {
            /*
        val controlAlpha = animateFloatAsState(
            targetValue = if (showControl.value) 1f else 0f,
            tween(200)
        )

        val buttonEnabled = remember("NowPlaying_buttonEnabled") {
            derivedStateOf { controlAlpha.value != 0f }
        }

        val translationButtonEnabled = remember("NowPlaying_translationButtonEnabled") {
            derivedStateOf { buttonEnabled.value && alpha.value != 0f }
        }*/

            val scope = rememberCoroutineScope()

            val alphaAnim = remember { Animatable(0f) }

            KeyedContent {
                LaunchedEffect(nowPageLambda()) {
                    val targetAlpha = if (nowPageLambda() == Lyric) 1f else 0f
                    scope.launch {
                        alphaAnim.animateTo(targetAlpha)
                    }
                }
            }

            val translationButtonEnabled = remember("NowPlaying_translationButtonEnabled") {
                derivedStateOf { showControl.value && alphaAnim.value != 0f }
            }

            println("重组：主功能区")

            // 歌词
            KeyedContent {

                Column(
                    Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            compositingStrategy =
                                CompositingStrategy.ModulateAlpha
                            this.alpha = alphaAnim.value
                        }
                ) {
                    println("重组：LyricView 外层 3")

                    Lyric(
                        lyricEntries = { lyricEntries.value },
                        weightLambda = { showControl.value },
                        translationLambda = { translation.value },
                        onBackClick = {
                            showControl.value = true
                            lastClickTime.longValue =
                                TimeUtils.getNowMills()
                        },
                        mainViewModel = mainViewModel,
                        mediaViewModel = mediaViewModel
                    )
                }
            }

            // 这是小把手
            KeyedContent {
                Column(Modifier.fillMaxWidth()) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(top = 20.dp), contentAlignment = Alignment.Center
                    ) {
                        Box(
                            Modifier
                                .overlayEffect()
                                .size(
                                    width = 32.dp,
                                    height = 4.5.dp
                                )
                                .background(Color(0x4DFFFFFF), RoundedCornerShape(2.25.dp))
                                .clip(RoundedCornerShape(2.25.dp))
                        )
                    }
                }
            }

            // 主 View
            KeyedContent {
                SharedTransitionLayout {
                    Crossfade(
                        targetState = nowPageLambda(),
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .padding(top = 22.dp)
                    ) {
                        //println("nowPage: ${nowPageLambda()}")
                        //println("nowPageIt: $it")
                        when (it) {
                            Album ->
                                Column(
                                    Modifier
                                        .fillMaxSize()
                                        .clickable(enabled = false, onClick = {})
                                ) {
                                    KeyedContent {
                                        Column(Modifier.fillMaxHeight(0.595f)) {
                                            val isVisible = nowPageLambda() == Album

                                            Album(
                                                modifier = Modifier.sharedElementWithCallerManagedVisibility(
                                                    sharedContentState = rememberSharedContentState(
                                                        key = ShareAlbumKey
                                                    ),
                                                    visible = isVisible
                                                ),
                                                albumUrl = { thisMusicPlaying.value?.thumb },
                                                isPlaying = isPlayingStatusLambda
                                            )
                                            AnimatedContent(
                                                targetState = thisMusicPlaying.value,
                                                transitionSpec = {
                                                    fadeIn() togetherWith fadeOut()
                                                }, modifier = Modifier.padding(horizontal = 32.dp)
                                            ) {
                                                Row(
                                                    Modifier
                                                        .fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Column(
                                                        Modifier
                                                            .fillMaxWidth()
                                                            .weight(1f)
                                                            .padding(end = 15.dp)
                                                    ) {
                                                        Text(
                                                            text = it?.title
                                                                ?: defaultTitle,/*
                                                        fontWeight = FontWeight.Bold,*/
                                                            fontSize = 19.5.sp,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis,
                                                            fontWeight = FontWeight.Medium
                                                        )
                                                        Text(
                                                            text = it?.artistsName
                                                                ?: defaultArtistsName,
                                                            fontSize = 18.5.sp,
                                                            modifier = Modifier.overlayEffect(),
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis,
                                                            color = Color.White.copy(alpha = 0.35f)
                                                        )
                                                    }

                                                    KeyedContent {
                                                        ActionButtonsRow {
                                                            it
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                            Lyric ->
                                Column(Modifier.fillMaxSize()) {
                                    KeyedContent {
                                        val isVisible = nowPageLambda() == Lyric
                                        PlayingBar(
                                            modifier = Modifier.sharedElementWithCallerManagedVisibility(
                                                sharedContentState = rememberSharedContentState(
                                                    key = ShareAlbumKey
                                                ),
                                                visible = isVisible
                                            ),
                                            albumUrlLambda = {
                                                thisMusicPlaying.value?.thumb
                                            },
                                            musicPlayingLambda = { thisMusicPlaying.value }) {
                                            nowPageOnChanged(Album)
                                        }
                                    }
                                }

                            PlayingList ->
                                KeyedContent {
                                    Column(
                                        Modifier
                                            .fillMaxSize()
                                            .clickable(enabled = false, onClick = {})
                                    ) {
                                        val isVisible = nowPageLambda() == PlayingList
                                        PlayingBar(
                                            modifier = Modifier.sharedElementWithCallerManagedVisibility(
                                                sharedContentState = rememberSharedContentState(
                                                    key = ShareAlbumKey
                                                ),
                                                visible = isVisible
                                            ),
                                            albumUrlLambda = {
                                                thisMusicPlaying.value?.thumb
                                            },
                                            musicPlayingLambda = { thisMusicPlaying.value }) {
                                            nowPageOnChanged(Album)
                                        }
                                        KeyedContent {
                                            PlayingList(
                                                shuffleModeEnabledLambda = { shuffleModeEnabled.value },
                                                shuffleModeOnChanged = { shuffleModeSet ->
                                                    shuffleModeEnabled.value = shuffleModeSet
                                                },
                                                repeatModeLambda = { repeatMode.intValue },
                                                repeatModeOnChanged = { repeatModeSet ->
                                                    repeatMode.intValue = repeatModeSet
                                                },
                                                thisMusicPlayingLambda = { thisMusicPlaying.value }
                                            )
                                        }
                                    }
                                }
                        }
                    }
                }
            }

            // 音乐控制
            KeyedContent {
                Column(
                    Modifier
                        .fillMaxSize()
                        .statusBarsPadding(), verticalArrangement = Arrangement.Bottom
                ) {
                    Box(
                        Modifier
                            /*.fillMaxHeight(0.385f)*/
                            .fillMaxHeight(0.437f)
                            .fillMaxWidth()
                    ) {
                        println("重组：控制区域外部")

                        KeyedContent {
                            if (showControl.value) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(top = 40.dp)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null,
                                            onClick = {
                                                //showControl.value = true
                                                /*lastClickTime.longValue =
                                                TimeUtils.getNowMills()*/
                                            })
                                )
                            }
                        }

                        KeyedContent {
                            Column(
                                Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                AnimatedVisibility(
                                    visible = showControl.value,
                                    enter = fadeIn() + expandVertically(
                                        expandFrom = Alignment.Top,
                                        initialHeight = { (it / 1.4).toInt() }),
                                    exit = fadeOut() + shrinkVertically(
                                        shrinkTowards = Alignment.Top,
                                        targetHeight = { (it / 1.4).toInt() })
                                ) {
                                    KeyedContent {
                                        Row(
                                            Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 32.dp)
                                                .graphicsLayer {
                                                    compositingStrategy =
                                                        CompositingStrategy.ModulateAlpha
                                                    this.alpha = alphaAnim.value
                                                },
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            KeyedContent {
                                                Box(
                                                    modifier = Modifier
                                                        .overlayEffect()
                                                        .alpha(0.4f)
                                                        .clickable(
                                                            enabled = translationButtonEnabled.value,
                                                            onClick = {
                                                                Vibrator.click(context)
                                                                translation.value =
                                                                    !translation.value
                                                                showControl.value = true
                                                                lastClickTime.longValue =
                                                                    TimeUtils.getNowMills()
                                                                SettingsLibrary.NowPlayingTranslation =
                                                                    translation.value
                                                            },
                                                            indication = null,
                                                            interactionSource = remember { MutableInteractionSource() }),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    AnimatedContent(
                                                        targetState = translation.value,
                                                        transitionSpec = {
                                                            fadeIn() togetherWith fadeOut()
                                                        }) {
                                                        if (it) {
                                                            Icon(
                                                                painterResource(id = R.drawable.ic_nowplaying_translateon),
                                                                contentDescription = null,
                                                                modifier = Modifier
                                                                    .size(30.dp)
                                                            )
                                                        } else {
                                                            Icon(
                                                                painterResource(id = R.drawable.ic_nowplaying_translate),
                                                                contentDescription = null,
                                                                modifier = Modifier
                                                                    .size(30.dp)
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    PlayerControl(
                                        isPlayingLambda = isPlayingStatusLambda,
                                        isPlayingOnChanged = isPlayingOnChanged,
                                        onPrevious = {
                                            mediaControl?.seekToPreviousMediaItem()
                                            showControl.value = true
                                            lastClickTime.longValue = TimeUtils.getNowMills()
                                        },
                                        onStatus = { status ->
                                            if (status) {
                                                mediaControl?.fadePlay()
                                            } else {
                                                mediaControl?.fadePause()
                                            }
                                            showControl.value = true
                                            lastClickTime.longValue = TimeUtils.getNowMills()
                                        },
                                        onNext = {
                                            mediaControl?.seekToNextMediaItem()
                                            showControl.value = true
                                            lastClickTime.longValue = TimeUtils.getNowMills()
                                        },
                                        onSeek = { position ->
                                            mediaControl?.seekTo(position.toLong())
                                        },
                                        onLyrics = {
                                            if (nowPageLambda() == Lyric) {
                                                nowPageOnChanged(Album)
                                            } else {
                                                nowPageOnChanged(Lyric)
                                            }
                                        },
                                        onPlaylist = {
                                            if (nowPageLambda() == PlayingList) {
                                                nowPageOnChanged(Album)
                                            } else {
                                                nowPageOnChanged(PlayingList)
                                            }
                                        },
                                        nowPage = {
                                            nowPageLambda()
                                        },
                                        onSlider = {
                                            showControl.value = true
                                            lastClickTime.longValue = TimeUtils.getNowMills()
                                        },
                                        modifier = Modifier
                                            /*.graphicsLayer {
                                                compositingStrategy =
                                                    CompositingStrategy.Offscreen
                                                //this.alpha = controlAlpha.value
                                            }*/
                                            .padding(top = 52.dp),
                                        onWhile = {
                                            shuffleModeEnabled.value =
                                                mediaControl?.shuffleModeEnabled ?: false
                                            repeatMode.intValue =
                                                mediaControl?.repeatMode ?: REPEAT_MODE_OFF
                                        })
                                }
                            }
                        }
                    }
                }
            }

        }
    }
