package com.rotatsulabs.axolotl.data.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.rotatsulabs.axolotl.data.libraries.Song

class ImageViewModel : ViewModel() {
    val recommendMusicList: MutableState<List<Song>> = mutableStateOf(emptyList())
}
