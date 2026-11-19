package com.rotatsulabs.axolotl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import com.tencent.mmkv.MMKV
import com.rotatsulabs.axolotl.core.data.viewmodel.MainViewModel
import com.rotatsulabs.axolotl.core.data.viewmodel.MediaViewModel

abstract class BaseActivity : ComponentActivity() /*{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }
}*/

