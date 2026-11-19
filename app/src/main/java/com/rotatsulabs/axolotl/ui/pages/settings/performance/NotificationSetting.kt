package com.rotatsulabs.axolotl.ui.pages.settings.performance

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.rotatsulabs.axolotl.R
import com.rotatsulabs.axolotl.code.MediaController
import com.rotatsulabs.axolotl.code.MediaController.mediaControl
import com.rotatsulabs.axolotl.code.PlaybackService
import com.rotatsulabs.axolotl.data.libraries.SettingsLibrary
import com.rotatsulabs.axolotl.ui.pages.settings.GroupSpacer
import com.rotatsulabs.axolotl.ui.pages.settings.ListHeader
import com.rotatsulabs.axolotl.ui.pages.settings.SettingBackground
import com.rotatsulabs.axolotl.ui.pages.settings.SwitchItem
import com.rotatsulabs.axolotl.ui.widgets.basic.RoundColumn
import com.rotatsulabs.axolotl.ui.widgets.basic.Title

@Composable
fun NotificationSetting(navController: NavController) =
    SettingBackground {
        Title(title = stringResource(id = R.string.settings_performance_notification_title),
            onBack = {
                navController.popBackStack()
            },
            content = {
                item("settings") {
                    Column(Modifier.fillMaxSize()) {
                        // ListHeader(content = stringResource(id = R.string.settings_performance_notification_basic))

                        RoundColumn {
                            SwitchItem(
                                title = stringResource(id = R.string.settings_performance_ui_notification_basic_enable_icon_title),
                                // desc = stringResource(id = R.string.settings_performance_ui_notification_basic_enable_icon_desc),
                                onClick = {
                                    SettingsLibrary.NotificationEnableIcon =
                                        !SettingsLibrary.NotificationEnableIcon
                                    MediaController.mediaControl?.let {
                                        PlaybackService().setCustomButtons(
                                            it
                                        )
                                    }
                                },
                                checkedLambda = { SettingsLibrary.NotificationEnableIcon }
                            )
                        }
                        ListHeader(content = stringResource(id = R.string.settings_performance_ui_notification_basic_enable_icon_desc))

                        GroupSpacer()

                        ListHeader(content = stringResource(id = R.string.settings_performance_notification_others))
                        RoundColumn {
                            SwitchItem(
                                title = stringResource(id = R.string.settings_performance_ui_notification_others_smaller_icon_title),
                                // desc = stringResource(id = R.string.settings_performance_ui_notification_others_smaller_icon_desc),
                                onClick = {
                                    SettingsLibrary.NotificationSmallerIcon =
                                        !SettingsLibrary.NotificationSmallerIcon
                                    mediaControl?.let { PlaybackService().setCustomButtons(it) }
                                },
                                checkedLambda = { SettingsLibrary.NotificationSmallerIcon }
                            )
                        }
                        ListHeader(content = stringResource(id = R.string.settings_performance_ui_notification_others_smaller_icon_desc))
                        GroupSpacer()
                    }
                }
            }
        )
    }