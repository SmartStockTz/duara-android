package com.fahamutech.duaracore.components

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.sharp.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fahamutech.duaracore.models.DuaraRemote
import com.fahamutech.duaracore.states.MaduaraState
import com.fahamutech.duaracore.utils.shareApp

@Composable
fun MaduaraTopBar(
    maduaraState: MaduaraState, context: Context, navController: NavController
) {
    var inSearchMode by remember { mutableStateOf(false) }
    Box {
        if (!inSearchMode) {
            TopBarNormal(maduaraState, context, navController) {
                inSearchMode = true
            }
        } else {
            TopBarSearch(maduaraState, context) {
                inSearchMode = false
            }
        }
    }
}

@Composable
private fun TopBarNormal(
    maduaraState: MaduaraState, context: Context,
    navController: NavController, onSearchClick: () -> Unit
) {
    val syncMaduaraProgress by maduaraState.maduaraSyncProgress.observeAsState()
    TopAppBar {
        IconButton(onClick = {
            navController.popBackStack()
        }) {
            Icon(Icons.Sharp.ArrowBack, "back")
        }
        Text(
            text = "Maduara",
            fontSize = 24.sp,
            fontWeight = FontWeight(500),
            lineHeight = 36.sp,
            modifier = Modifier.absolutePadding(16.dp)
        )
        Spacer(modifier = Modifier.weight(1.0f))
        IconButton(onClick = {
            shareApp(context)
        }) {
            Icon(Icons.Default.Share, contentDescription = "share")
        }
        IconButton(onClick = {
            onSearchClick()
        }) {
            Icon(Icons.Default.Search, contentDescription = "tafuta")
        }
        Box(contentAlignment = Alignment.Center) {
            if (syncMaduaraProgress == true) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier
                        .absolutePadding(8.dp, 0.dp, 8.dp, 0.dp)
                        .size(24.dp)

                )
            } else {
                IconButton(onClick = {
//                    maduaraState.syncMaduara(context)
                    maduaraState.syncMaduara(context)
                }) {
                    Icon(Icons.Default.Refresh, contentDescription = "vuta")
                }
            }
        }
    }
}

@Composable
private fun TopBarSearch(
    maduaraState: MaduaraState, context: Context, onSearchClose: () -> Unit
) {
//    var maduaraFilter by maduaraState.maduaraFilter.observeAsState()
    var searchKeyword by remember { mutableStateOf("") }
    val focusRequester = FocusRequester()
    TopAppBar {
        IconButton(onClick = {
            onSearchClose()
            maduaraState.searchDuara("", context)
        }) {
            Icon(Icons.Default.Close, "close search")
        }
        Box(
            modifier = Modifier
                .absolutePadding(4.dp, 4.dp, 8.dp, 4.dp)
                .background(shape = RoundedCornerShape(4.dp), color = Color(0xE9ECECEC))
                .fillMaxWidth()
                .height(38.dp),
            contentAlignment = Alignment.Center

        ) {
            BasicTextField(
                value = searchKeyword,
                onValueChange = {
                    searchKeyword = it
                    maduaraState.searchDuara(searchKeyword, context)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .absolutePadding(8.dp)
                    .focusRequester(focusRequester),
                maxLines = 1,
                singleLine = true,
            )
        }
    }
    LaunchedEffect("tafuta") {
        focusRequester.requestFocus()
    }
}

@Composable
fun HelperMessage() {
    Text(
        text = "Chagua mtoa huduma wa kuongea nae.",
        fontWeight = FontWeight(300),
        fontSize = 14.sp,
        modifier = Modifier
            .absolutePadding(16.dp, 8.dp, 16.dp, 8.dp)
            .fillMaxWidth()
    )
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun MaduaraList(
    maduara: List<DuaraRemote>, navController: NavController, context: Context
) {
    val st = rememberLazyListState()
    LazyColumn(
//        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
//        cells = GridCells.Adaptive(minSize = 102.dp),
        state = st
    ) {
        maduara.groupBy { it.category }.forEach { (category, contents) ->
            stickyHeader {
                Box(
                    modifier = Modifier
                        .background(Color(0xFFDADADA))
                        .absolutePadding(16.dp, 8.dp, 16.dp, 8.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = category, style= TextStyle(
                        color = Color.Black
                    ))
                }
            }
            items(contents) { d ->
                DuaraMemberItem(d, navController, context)
            }
        }
    }
}

