@file:Suppress("DEPRECATION")

package com.rotatsulabs.axolotl.core.ui.pages.nowplaying

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rotatsulabs.axolotl.core.data.library.Song
import com.rotatsulabs.axolotl.core.data.model.artistsName
import com.rotatsulabs.axolotl.core.data.model.defaultArtistsName
import com.rotatsulabs.axolotl.core.data.model.defaultTitle
import com.rotatsulabs.axolotl.core.ui.widgets.basic.ImageQuality
import com.rotatsulabs.axolotl.core.ui.widgets.basic.ShadowImageWithCache
import com.rotatsulabs.axolotl.core.ui.widgets.basic.KeyedContent
import com.rotatsulabs.axolotl.core.ui.widgets.effects.ShadowType
import com.rotatsulabs.axolotl.core.ui.widgets.effects.overlayEffect

@Composable
internal fun PlayingBar(
    modifier: Modifier,
    albumUrlLambda: () -> Uri?,
    musicPlayingLambda: () -> Song?,
    onAlbumClick: () -> Unit
) = KeyedContent {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.5.dp)
            .padding(top = 22.dp)
            .height(70.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        ShadowImageWithCache(
            dataLambda = albumUrlLambda, contentDescription = null, modifier = modifier
                .size(69.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = {
                        onAlbumClick()
                    }), cornerRadius = 5.dp,
            imageQuality = ImageQuality.LOW,
            shadowType = ShadowType.Small,
            shadowOverlay = true
        )
        Column(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(start = 12.dp, end = 15.dp)
        ) {
            Text(
                text = musicPlayingLambda()?.title ?: defaultTitle,/*
                fontWeight = FontWeight.Bold,*/
                fontSize = 16.5.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Medium,
                lineHeight = 16.5.sp
            )
            Text(
                text = musicPlayingLambda()?.artistsName
                    ?: defaultArtistsName,
                fontSize = 15.sp,
                modifier = Modifier.overlayEffect(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.White.copy(alpha = 0.35f)
            )
        }

        KeyedContent {
            ActionButtonsRow(musicPlayingLambda)
        }
    }

}
