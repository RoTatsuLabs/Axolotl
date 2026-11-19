package com.rotatsulabs.axolotl.ui.pages.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.rotatsulabs.axolotl.core.data.viewmodel.ImageViewModel
import com.rotatsulabs.axolotl.ui.pages.library.Library
import com.rotatsulabs.axolotl.ui.widgets.basic.KeyedContent
import com.rotatsulabs.axolotl.ui.R

/*@Stable
object HomePage {
    const val Home = "主页"
    const val Library = "资料库"
}*/

@Composable
fun HomeNav(
    navController: NavController,
    pagerState: PagerState,
    imageViewModel: ImageViewModel,
    nowPageOnChanged: (String) -> Unit
) =
    KeyedContent {
        val context = LocalContext.current
        val home = context.getString(R.string.page_home_title)
        val library = context.getString(R.string.page_library_title)

        //val pagerState = rememberPagerState(pageCount = { 2 })
        /*val nowPageIndex = when (nowPage.value) {
            home -> 0
            library -> 1
            else -> 0
        }

        KeyedContent {
            LaunchedEffect(nowPageIndex) {
                pagerState.animateScrollToPage(nowPageIndex)
            }
        }*/

        KeyedContent {
            LaunchedEffect(pagerState) {
                nowPageOnChanged(
                    when (pagerState.currentPage) {
                    0 -> home
                    1 -> library
                    else -> home
                    }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            beyondViewportPageCount = 2,
            key = { page -> page },
            userScrollEnabled = false
        ) { page ->
            when (page) {
                0 -> Home(navController, imageViewModel)
                1 -> Library(navController)
            }
        }
    }
