package com.rotatsulabs.axolotl.core.ui.widgets.basic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.key

@Composable
fun KeyedContent(content: @Composable () -> Unit) =
    key(content.hashCode()) {
        content()
    }