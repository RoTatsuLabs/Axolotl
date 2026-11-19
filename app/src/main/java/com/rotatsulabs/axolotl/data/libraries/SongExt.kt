package com.rotatsulabs.axolotl.data.libraries

val Song.artistsList: List<String>?
    get() = this.artists?.toMultipleArtists()

val Song.artistsName: String?
    get() = this.artistsList?.toArtistsString()