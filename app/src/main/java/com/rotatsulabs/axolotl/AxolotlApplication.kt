package com.rotatsulabs.axolotl

import android.app.Application
import android.content.ComponentName
import android.net.Uri
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.funny.data_saver.core.DataSaverConverter.registerTypeConverters
import com.google.common.util.concurrent.MoreExecutors
import com.google.gson.GsonBuilder
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.rotatsulabs.axolotl.core.playback.MediaController.mediaControl
import com.rotatsulabs.axolotl.core.playback.MediaController.playingMusicList
import com.rotatsulabs.axolotl.core.playback.PlaybackService
import com.rotatsulabs.axolotl.core.data.library.Folder
import com.rotatsulabs.axolotl.core.data.library.MusicLibrary
import com.rotatsulabs.axolotl.core.data.library.PlayList
import com.rotatsulabs.axolotl.core.data.library.Song
import com.rotatsulabs.axolotl.core.data.library.StringWrapper
import com.rotatsulabs.axolotl.core.data.storage.UriTypeAdapter
import kotlin.system.exitProcess

class AxolotlApplication : Application() {
    override fun onCreate() {

        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            e.printStackTrace()
            CrashActivity.startActivity(this, e.stackTraceToString())
            android.os.Process.killProcess(android.os.Process.myPid())
            exitProcess(1)
        }

        // 初始化 MMKV
        MMKV.initialize(this)

        val gson =
            GsonBuilder()
            //.registerTypeAdapter(Uri::class.java, UriSerializer())
            //registerTypeAdapter(Uri::class.java, UriDeserializer())
            .registerTypeAdapter(Uri::class.java, UriTypeAdapter())
            .create()

        registerTypeConverters(
            save = { bean -> gson.toJson(bean) },
            restore = { str -> gson.fromJson(str, Folder::class.java) }
        )

        /*registerTypeConverters(
            save = { bean -> gson.toJson(bean) },
            restore = { str -> gson.fromJson(str, ImmutableList::class.java) }
        )

        registerTypeConverters(
            save = { bean -> gson.toJson(bean) },
            restore = { str -> gson.fromJson(str, ArrayList::class.java) }
        )*/

        registerTypeConverters(
            save = { bean -> gson.toJson(bean) },
            restore = { str -> gson.fromJson(str, PlayList::class.java) }
        )

        registerTypeConverters(
            save = { bean -> gson.toJson(bean) },
            restore = { str -> gson.fromJson(str, IntArray::class.java) }
        )

        registerTypeConverters(
            save = { bean -> gson.toJson(bean) },
            restore = { str -> gson.fromJson(str, Song::class.java) }
        )

        registerTypeConverters(
            save = { bean -> gson.toJson(bean) },
            restore = { str -> gson.fromJson(str, StringWrapper::class.java) }
        )

        // 初始化媒体控制器
        val sessionToken = SessionToken(this, ComponentName(this, PlaybackService::class.java))
        val controllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
        controllerFuture.addListener(
            {
                mediaControl = controllerFuture.get()

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val playListData = MusicLibrary.loadPlayList()
                        val playStatusData = MusicLibrary.loadPlayStatus()

                        println("prepare 读取历史")
                        if (playListData.mainMusicList != null) {
                            println("prepare 准备调用")
                            /*launch {
                                runCatching {
                                    // 无论设置与否，先还原
                                    val thisMainMusicList = playListData.mainMusicList
                                    mainMusicList.value = thisMainMusicList
                                    println("prepare 恢复主歌单")
                                }
                            }*/

                            val statusMusic = playStatusData.music
                            if (statusMusic != null) {
                                com.rotatsulabs.axolotl.core.playback.MediaController.prepare(
                                    statusMusic,
                                    playListData.playingMusicList!!,
                                    playStatusData.position,
                                    playStatusData.shuffleModeEnabled,
                                    playStatusData.repeatMode,
                                    false
                                )
                            }

                            if (playListData.playingMusicList != null) {
                                playingMusicList.value = playListData.playingMusicList
                            }
                        }
                    } catch (e:Exception) {
                        e.printStackTrace()
                    }
                }
            },
            MoreExecutors.directExecutor()
        )

        super.onCreate()
    }
}


/*
class ImmutableListTypeAdapter<T> : JsonSerializer<ImmutableList<T>>,
    JsonDeserializer<ImmutableList<T>> {
    override fun serialize(src: ImmutableList<T>?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return context?.serialize(src?.toList()) ?: JsonNull.INSTANCE
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): ImmutableList<T> {
        val listType = object : TypeToken<List<T>>() {}.type
        val list = context?.deserialize<List<T>>(json, listType)
        return ImmutableList.copyOf(list)
    }
}*/

/*
class UriSerializer : JsonSerializer<Uri> {
    override fun serialize(src: Uri?, typeOfSrc: Type?, context: com.google.gson.JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src.toString())
    }
}

class UriDeserializer : JsonDeserializer<Uri> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: com.google.gson.JsonDeserializationContext?): Uri {
        return Uri.parse(json?.asString)
    }
}*/
