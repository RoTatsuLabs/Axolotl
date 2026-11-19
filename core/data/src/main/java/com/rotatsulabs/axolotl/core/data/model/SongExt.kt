package com.rotatsulabs.axolotl.core.data.model
import com.rotatsulabs.axolotl.core.data.library.Song

val Song.artistsList: List<String>?
    get() = this.artists?.toMultipleArtists()

val Song.artistsName: String?
    get() = this.artistsList?.toArtistsString()