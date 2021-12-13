package com.fahamutech.duara.components

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.sharp.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fahamutech.duara.R
import com.fahamutech.duara.models.DuaraRemote
import com.fahamutech.duara.models.DuaraLocal
import com.fahamutech.duara.services.countWaliomoKwenyeDuara
import com.fahamutech.duara.services.getMaduaraByDuaraNumberHash
import com.fahamutech.duara.states.MaduaraState
import com.fahamutech.duara.utils.duaraLocalToRemoteHash
import com.fahamutech.duara.utils.stringToSHA256
import kotlinx.coroutines.launch

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
        text = "Chagua duara kuona waliomo na uchague " +
                "wa kuongea nae.",
        fontWeight = FontWeight(300),
        fontSize = 14.sp,
        modifier = Modifier
            .absolutePadding(16.dp, 8.dp, 16.dp, 8.dp)
            .fillMaxWidth()
    )
}

@ExperimentalMaterialApi
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MaduaraLocalList(
    grouped: Map<Char, List<DuaraLocal>>,
    maduaraState: MaduaraState,
    showModalSheet: (List<DuaraRemote>) -> Unit
) {
    val st = rememberLazyListState()
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxWidth(),
        state = st
    ) {
        grouped.forEach { (initial, duaraLocalByInitial) ->
            stickyHeader {
                Text(initial.toString())
            }
            items(duaraLocalByInitial) { duaraLocal ->
                MaduaraLocalItem(duaraLocal, maduaraState, showModalSheet)
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun MaduaraLocalItem(
    i: DuaraLocal,
    maduaraState: MaduaraState,
    showModalSheet: (List<DuaraRemote>) -> Unit
) {
    val scope = rememberCoroutineScope()
    var waliomo by remember { mutableStateOf(1) }
    val message = waliomoMessage(waliomo)
    MaduaraLocalItemView(i, message) {
        scope.launch {
            showDuaraMembersOrPromo(i, maduaraState, showModalSheet)
        }
    }
    LaunchedEffect(i.normalizedNumber) {
        scope.launch {
            waliomo = countDuaraMembers(i.normalizedNumber)
        }
    }
}

private suspend fun showDuaraMembersOrPromo(
    duaraLocal: DuaraLocal,
    maduaraState: MaduaraState,
    showModalSheet: (List<DuaraRemote>) -> Unit
) {
    val b = duaraLocalToRemoteHash(duaraLocal.normalizedNumber)
    val c = getMaduaraByDuaraNumberHash(b)
    if (c.isEmpty()) {
        maduaraState.toggleShowOneMemberDialog(true)
    } else {
        showModalSheet(c)
    }
}

@Composable
private fun MaduaraLocalItemView(
    i: DuaraLocal, message: String, onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .absolutePadding(0.dp, 0.dp, 0.dp, 8.dp)
            .clickable { onClick() }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.absolutePadding(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_list_item_bg),
                contentDescription = "logo"
            )
            Text(
                text = i.name[0].toString(),
                fontWeight = FontWeight(500),
                color = Color.White,
                fontSize = 24.sp
            )
        }
        Column(
            modifier = Modifier.absolutePadding(8.dp, 0.dp, 8.dp, 0.dp)
        ) {
            Text(
                text = i.name,
                fontWeight = FontWeight(500),
                fontSize = 16.sp
            )
            Text(
                text = i.normalizedNumber,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = Color(0xFF747272)
            )
            Text(
                text = message,
                fontWeight = FontWeight(300),
                fontSize = 14.sp,
                color = Color(0xFF747272)
            )
        }
    }
}

private suspend fun countDuaraMembers(normalizedNumber: String): Int {
    val a = stringToSHA256(normalizedNumber)
    val b = stringToSHA256(a)
    return countWaliomoKwenyeDuara(b)
}

private fun waliomoMessage(waliomo: Int): String {
    var message = "Upo mwenyewe."
    if (waliomo > 0) {
        message = "Wewe na $waliomo wengine."
    }
    return message
}
