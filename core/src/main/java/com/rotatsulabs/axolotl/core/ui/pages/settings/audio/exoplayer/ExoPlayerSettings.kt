package com.rotatsulabs.axolotl.core.ui.pages.settings.audio.exoplayer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.rotatsulabs.axolotl.core.data.settings.SettingsLibrary
import com.rotatsulabs.axolotl.core.ui.navigation.UI
import com.rotatsulabs.axolotl.core.ui.pages.settings.Divider
import com.rotatsulabs.axolotl.core.ui.pages.settings.GroupSpacer
import com.rotatsulabs.axolotl.core.ui.pages.settings.GroupSpacerMedium
import com.rotatsulabs.axolotl.core.ui.pages.settings.LabelItem
import com.rotatsulabs.axolotl.core.ui.pages.settings.ListHeader
import com.rotatsulabs.axolotl.core.ui.pages.settings.SelectItem
import com.rotatsulabs.axolotl.core.ui.pages.settings.SettingBackground
import com.rotatsulabs.axolotl.core.ui.pages.settings.SwitchItem
import com.rotatsulabs.axolotl.core.ui.widgets.basic.RoundColumn
import com.rotatsulabs.axolotl.core.ui.widgets.basic.Title
import com.rotatsulabs.axolotl.core.R

@Composable
fun ExoPlayerSettings(navController: NavController) =
    SettingBackground {
        Title(title = stringResource(id = R.string.settings_audio_exoplayer),
            subTitle = stringResource(id = R.string.settings_audio_exoplayer_sub),
            onBack = {
                navController.popBackStack()
            },
            content = {
                item("settings") {
                    Column(Modifier.fillMaxSize()) {

                        ListHeader(stringResource(id = R.string.settings_audio_exoplayer_behaviors))
                        RoundColumn {
                            SwitchItem(
                                title = stringResource(id = R.string.settings_audio_exoplayer_behaviors_audio_attributes),
                                // desc = stringResource(id = R.string.settings_audio_exoplayer_behaviors_audio_attributes_desc),
                                onClick = {
                                    SettingsLibrary.AudioAttributes =
                                        !SettingsLibrary.AudioAttributes
                                },
                                checkedLambda = { SettingsLibrary.AudioAttributes }
                            )
                        }
                        ListHeader(content = stringResource(id = R.string.settings_audio_exoplayer_behaviors_audio_attributes_desc))

                        GroupSpacer()

                        ListHeader(stringResource(id = R.string.settings_audio_exoplayer_decode))
                        RoundColumn {
                            SelectItem(
                                title = stringResource(id = R.string.settings_audio_exoplayer_decode_codec),
                                items = listOf(
                                    "Auto",
                                    "FFmpeg",
                                    "System"
                                ),
                                value = SettingsLibrary.Codec,
                                onValueChange = {
                                    SettingsLibrary.Codec = it
                                }
                            )

                            Divider()

                            LabelItem(title = stringResource(id = R.string.settings_audio_exoplayer_support_mediacodec)) {
                                navController.navigate(UI.Settings.MediaCodec)
                            }

                            Divider()

                            SwitchItem(
                                title = stringResource(id = R.string.settings_audio_exoplayer_decode_hardware_audio_track_playback_params),
                                onClick = {
                                    SettingsLibrary.HardwareAudioTrackPlayBackParams =
                                        !SettingsLibrary.HardwareAudioTrackPlayBackParams
                                },
                                checkedLambda = { SettingsLibrary.HardwareAudioTrackPlayBackParams }
                            )
                        }

                        GroupSpacerMedium()

                        RoundColumn {
                            SwitchItem(
                                title = stringResource(id = R.string.settings_audio_exoplayer_decode_audio_float_output),
                                // desc = stringResource(id = R.string.settings_audio_exoplayer_decode_audio_float_output_desc),
                                onClick = {
                                    SettingsLibrary.AudioFloatOutput =
                                        !SettingsLibrary.AudioFloatOutput
                                },
                                checkedLambda = { SettingsLibrary.AudioFloatOutput }
                            )
                        }
                        ListHeader(content = stringResource(id = R.string.settings_audio_exoplayer_decode_audio_float_output_desc))

                        GroupSpacer()
                    }

                }
            }
        )
    }
