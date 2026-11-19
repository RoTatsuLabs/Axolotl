package com.rotatsulabs.axolotl.data.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.rotatsulabs.axolotl.data.libraries.YosMediaItem

class ImageViewModel : ViewModel() {
    val recommendMusicList: MutableState<List<YosMediaItem>> = mutableStateOf(emptyList())
}
