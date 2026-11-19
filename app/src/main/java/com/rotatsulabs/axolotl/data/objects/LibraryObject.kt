package com.rotatsulabs.axolotl.data.objects

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import com.rotatsulabs.axolotl.data.libraries.Folder
import com.rotatsulabs.axolotl.data.libraries.Song

@Stable
object LibraryObject {
    @Stable
    private val targetAlbumName = mutableStateOf("")
    fun setTargetAlbumName(name: String) {
        targetAlbumName.value = name
    }

    fun getTargetAlbumName(): String {
        return targetAlbumName.value
    }

    @Stable
    private val targetList: MutableState<List<Song>> = mutableStateOf(emptyList())
    @Stable
    private val targetListTitle = mutableStateOf("")
    fun setTargetListWithTitle(title: String, list: List<Song>) {
        targetListTitle.value = title
        targetList.value = list
    }
    fun getTargetListWithTitle(): Pair<String, List<Song>> {
        return Pair(targetListTitle.value, targetList.value)
    }
}