package com.rotatsulabs.axolotl

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.akane.libphonograph.hasScopedStorageWithMediaTypes
import com.rotatsulabs.axolotl.core.data.library.MusicLibrary
import com.rotatsulabs.axolotl.core.data.settings.SettingsLibrary
import com.rotatsulabs.axolotl.core.data.viewmodel.ImageViewModel
import com.rotatsulabs.axolotl.core.data.viewmodel.MainViewModel
import com.rotatsulabs.axolotl.core.data.viewmodel.MediaViewModel
import com.rotatsulabs.axolotl.core.ui.widgets.basic.KeyedContent
import java.io.File

/*//MediaPlayer全局控制器
var mediaController = com.rotatsulabs.axolotl.core.playback.MediaController*/

class MainActivity : BaseActivity() {

    private val mediaViewModel: MediaViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()

    private val imageViewModel: ImageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            AxolotlApp(
                mainViewModel = mainViewModel,
                mediaViewModel = mediaViewModel,
                imageViewModel = imageViewModel,
                permissionRequest = { CheckAndRequestPermission() }
            )
        }
    }

        @Composable
    fun CheckAndRequestPermission() {
        val context = LocalContext.current
        val requestPermissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val isGranted = permissions.entries.all { it.value }
            if (isGranted) {
                // Load music list here
                loadMusic(context, enforce = true)
                sendBroadcast(Intent("com.rotatsulabs.axolotl.BLUETOOTH_STATUS_REFRESH"))
            } else {
                // Set music list to empty if permission is denied
                // mainMusicList.value = mutableListOf()
            }
        }

        KeyedContent {
            LaunchedEffect(Unit) {

                var permissions = emptyArray<String>()

                if ((hasScopedStorageWithMediaTypes()
                            && ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_MEDIA_AUDIO
                    ) != PackageManager.PERMISSION_GRANTED)
                    /*|| (!hasScopedStorageV2()
                            && ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED)*/
                    || (!hasScopedStorageWithMediaTypes()
                            && ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED)
                ) {
                    permissions += arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

                    if (hasScopedStorageWithMediaTypes()) {
                        permissions += arrayOf(Manifest.permission.READ_MEDIA_AUDIO)
                    }
                }

                if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        permissions += Manifest.permission.BLUETOOTH_CONNECT
                }

                if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissions += Manifest.permission.POST_NOTIFICATIONS
                }

                if (permissions.isNotEmpty()) {
                    requestPermissionLauncher.launch(permissions)
                }
                else {
                    loadMusic(context)
                }
            }
        }
    }


    /*LaunchedEffect(permissionState.value) {
            val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_MEDIA_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            }

            if (hasPermission) {
                permissionState.value = PermissionState.DENIED
                loadMusic(context)
            }
        }

        val showDialog = remember("CheckPermission_showDialog") {
            derivedStateOf { permissionState.value != PermissionState.DENIED  }
        }

        if (showDialog.value) {
            val dialogProperties = ModalBottomSheetProperties(
                securePolicy = SecureFlagPolicy.Inherit,
                isFocusable = true,
                shouldDismissOnBackPress = false
            )

            val bottomSheetState = rememberModalBottomSheetState()
            val scope = rememberCoroutineScope()

            KeyedContent {
                OptionDialog(
                    title = stringResource(id = CoreR.string.permission_grant_title),
                    subTitle = stringResource(id = CoreR.string.permission_grant_subtitle),
                    content = {
                        Text(text = stringResource(id = CoreR.string.permission_grant_desc))
                    },
                    positiveContent = stringResource(id = CoreR.string.permission_grant_button_positive),
                    properties = dialogProperties,
                    bottomSheetState = bottomSheetState,
                    onPositive = {
                        scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                            if (!bottomSheetState.isVisible) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_AUDIO)
                                } else {
                                    requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                                }
                                permissionState.value = PermissionState.DENIED
                            }
                        }
                    },
                    negativeContent = stringResource(id = CoreR.string.permission_grant_button_negative),
                    onNegative = {
                        scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                            if (!bottomSheetState.isVisible) {
                                mainMusicList.value = mutableListOf()
                                permissionState.value = PermissionState.DENIED
                            }
                        }
                    }
                ) {
                    mainMusicList.value = mutableListOf()
                    permissionState.value = PermissionState.DENIED
                }
            }
        }*/

    /*enum class PermissionState {
        UNKNOWN,
        GRANTED,
        DENIED
    }*/

    private fun loadMusic(context: Context, enforce: Boolean = false) {
        val needRefresh = SettingsLibrary.RefreshEveryTime
        if (needRefresh || enforce) {
            mediaViewModel.viewModelScope.launch(Dispatchers.IO) {
                // Application中已还原，这里算是后台扫描
                // mainMusicList.value = MusicScanner(context).getMusicList()
                MusicLibrary.scanMedia(context)
                println("刷新媒体库")
            }
        }
    }

}

fun readFile(path: String): String? {
    return try {
        File(path).readText()
    } catch (e: Exception) {
        //e.printStackTrace()
        null
    }
}