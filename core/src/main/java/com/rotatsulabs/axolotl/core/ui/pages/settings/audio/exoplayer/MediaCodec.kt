package com.rotatsulabs.axolotl.core.ui.pages.settings.audio.exoplayer

import android.media.MediaCodecList
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.rotatsulabs.axolotl.core.ui.pages.settings.DefaultItem
import com.rotatsulabs.axolotl.core.ui.pages.settings.SettingBackground
import com.rotatsulabs.axolotl.core.ui.widgets.basic.Title
import com.rotatsulabs.axolotl.core.ui.widgets.basic.roundColumn
import com.rotatsulabs.axolotl.core.R

@Composable
fun MediaCodec(navController: NavController) =
    SettingBackground {
        val codecInfos = remember("MediaCodec_codecInfos") {
            mutableStateOf(MediaCodecList(MediaCodecList.ALL_CODECS).codecInfos.filter { !it.isEncoder })
        }
        Title(title = stringResource(id = R.string.settings_audio_exoplayer_support_mediacodec),
            onBack = {
                navController.popBackStack()
            },
            content = {
                roundColumn {
                    itemsIndexed(
                        codecInfos.value,
                        key = { _, codec -> codec.name }/*,
                        contentType = { it.name }*/
                    ) { index, it ->
                        DefaultItem(
                            title = it.supportedTypes.joinToString(", "),
                            onClick = null,
                            desc = it.name
                        )

                        key(index) {
                            val needDivider = index < codecInfos.value.size - 1
                            if (needDivider) {
                                com.rotatsulabs.axolotl.core.ui.pages.settings.Divider()
                            }
                        }
                    }
                }
            }
        )
    }
