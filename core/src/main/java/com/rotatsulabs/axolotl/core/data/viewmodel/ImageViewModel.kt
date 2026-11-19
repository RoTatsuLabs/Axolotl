package com.rotatsulabs.axolotl.core.data.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.rotatsulabs.axolotl.core.data.library.Song

class ImageViewModel : ViewModel() {
    val recommendMusicList: MutableState<List<Song>> = mutableStateOf(emptyList())
}
