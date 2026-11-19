package com.rotatsulabs.axolotl.data.libraries

import android.net.Uri
import android.os.Parcelable
import androidx.compose.runtime.Stable
import com.funny.data_saver.core.mutableDataSaverListStateOf
import kotlinx.parcelize.Parcelize
import com.rotatsulabs.axolotl.data.PlayListSaver
import java.util.UUID

@Stable
@Parcelize
data class PlayList(
    val listID: String,
    val name: String,
    val songDataList: List<Uri>
) : Parcelable

@Stable
object PlayListLibrary {

    @Stable
    var playList by mutableDataSaverListStateOf(
        dataSaverInterface = PlayListSaver,
        key = "yos_play_list",
        initialValue = listOf<PlayList>()
    )
        private set

    fun PlayList.addMusic(music: Song) {
        playList += copy(songDataList = songDataList.toMutableList().apply {
            music.uri?.let { add(it) }
        })
        playList -= this
    }

    fun PlayList.removeMusic(music: Song) {
        playList += copy(songDataList = songDataList.toMutableList().apply {
            val songToRemove = songDataList.find { data ->
                music.uri == data
            }
            remove(songToRemove)
        })
        playList -= this
    }

    fun PlayList.rename(name: String) {
        playList += this.copy(name = name)
        playList -= this
    }

    fun create(name: String) {
        if (!playList.any { it.name == name }) {
            playList += PlayList(UUID.randomUUID().toString(), name, listOf())
        }
    }

    fun remove(list: PlayList) {
        playList -= list
    }
}

@Stable
object FavPlayListLibrary {
    @Stable
    var favPlayList by mutableDataSaverListStateOf(
        dataSaverInterface = PlayListSaver,
        key = "yos_fav_play_list",
        initialValue = listOf<Song>()
    )
        private set

    fun addMusic(music: Song) {
        if (!favPlayList.any { it.uri == music.uri }) {
            favPlayList += music
        }
    }

    fun removeMusic(music: Song) {
        favPlayList -= music
    }

    fun isFavorite(music: Song): Boolean = favPlayList.any { it.uri == music.uri }
}