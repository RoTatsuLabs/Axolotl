package com.rotatsulabs.axolotl.ui.pages.settings.performance

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.rotatsulabs.axolotl.core.data.settings.SettingsLibrary
import com.rotatsulabs.axolotl.ui.pages.settings.GroupSpacer
import com.rotatsulabs.axolotl.ui.pages.settings.GroupSpacerMedium
import com.rotatsulabs.axolotl.ui.pages.settings.ListHeader
import com.rotatsulabs.axolotl.ui.pages.settings.SelectItem
import com.rotatsulabs.axolotl.ui.pages.settings.SettingBackground
import com.rotatsulabs.axolotl.ui.pages.settings.SwitchItem
import com.rotatsulabs.axolotl.ui.widgets.basic.RoundColumn
import com.rotatsulabs.axolotl.ui.widgets.basic.Title
import com.rotatsulabs.axolotl.ui.R

@Composable
fun LyricSetting(navController: NavController) =
    SettingBackground {
        Title(title = stringResource(id = R.string.settings_performance_lyric_title),
            onBack = {
                navController.popBackStack()
            },
            content = {
                item("settings") {
                    Column(Modifier.fillMaxSize()) {
                        // GroupSpacerMedium()
                        ListHeader(content = stringResource(id = R.string.settings_performance_lyric_style))

                        RoundColumn {
                            SelectItem(
                                title = stringResource(id = R.string.settings_performance_lyric_style_font_weight),
                                // desc = stringResource(id = R.string.settings_performance_lyric_style_font_weight_desc),
                                items = listOf(
                                    "Thin",
                                    "ExtraLight",
                                    "Light",
                                    "Regular",
                                    "Medium",
                                    "SemiBold",
                                    "Bold",
                                    "ExtraBold",
                                    "Black"
                                ),
                                value = SettingsLibrary.LyricFontWeight,
                                onValueChange = {
                                    SettingsLibrary.LyricFontWeight = it
                                }
                            )
                        }
                        ListHeader(content = stringResource(id = R.string.settings_performance_lyric_style_font_weight_desc))

                        GroupSpacerMedium()

                        RoundColumn {
                            SwitchItem(
                                title = stringResource(id = R.string.settings_performance_lyric_line_balance),
                                // desc = stringResource(id = R.string.settings_performance_lyric_line_balance_desc),
                                onClick = {
                                    SettingsLibrary.LyricLineBalance =
                                        !SettingsLibrary.LyricLineBalance
                                },
                                checkedLambda = { SettingsLibrary.LyricLineBalance }
                            )
                        }
                        ListHeader(content = stringResource(id = R.string.settings_performance_lyric_line_balance_desc))

                        GroupSpacer()

                        ListHeader(content = stringResource(id = R.string.settings_performance_lyric_others))

                        RoundColumn {
                            SwitchItem(
                                title = stringResource(id = R.string.settings_performance_lyric_blur_effect),
                                // desc = stringResource(id = R.string.settings_performance_lyric_blur_effect_desc),
                                onClick = {
                                    SettingsLibrary.LyricBlurEffect =
                                        !SettingsLibrary.LyricBlurEffect
                                },
                                checkedLambda = { SettingsLibrary.LyricBlurEffect }
                            )
                        }

                        ListHeader(content = stringResource(id = R.string.settings_performance_lyric_blur_effect_desc))
                        GroupSpacer()
                    }
                }
            }
        )
    }