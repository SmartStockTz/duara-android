package com.fahamutech.duara.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.sharp.ArrowBack
import androidx.compose.material.icons.sharp.Search
import androidx.compose.material.icons.sharp.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.fahamutech.duara.models.Ongezi
import com.fahamutech.duara.states.OngeziState
import com.fahamutech.duara.ui.theme.DuaraBlue

@Composable
fun OngeziTopBar(
    ongezi: Ongezi, ongeziState: OngeziState,
    context: Context, navController: NavController
) {
    var inSearchMode by remember { mutableStateOf(false) }
    Box {
        if (!inSearchMode) {
            OngeziTopBarNormal(ongezi, navController) {
                inSearchMode = true
            }
        } else {
            OngeziTopBarSearch(ongeziState, context) {
                inSearchMode = false
            }
        }
    }
}

@Composable
private fun OngeziTopBarNormal(
    ongezi: Ongezi,
    navController: NavController, onSearchClick: () -> Unit
) {
    TopAppBar {
        IconButton(onClick = {
            navController.popBackStack()
        }) {
            Icon(Icons.Sharp.ArrowBack, "back")
        }
        Text(
            text = ongezi.duara!!.nickname,
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
    }
}

@Composable
private fun OngeziTopBarSearch(
    ongeziState: OngeziState, context: Context, onSearchClose: () -> Unit
) {
    var searchKeyword by remember { mutableStateOf("") }
    val focusRequester = FocusRequester()
    TopAppBar {
        IconButton(onClick = {
            onSearchClose()
//            maduaraState.searchDuara("", context)
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
//                    maduaraState.searchDuara(searchKeyword, context)
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
fun OngeziBody() {
    val s = rememberScrollState()
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
//            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            // try to consume before LazyColumn to collapse toolbar if needed, hence pre-scroll
//                val delta = available.y
//                val newOffset = toolbarOffsetHeightPx.value + delta
//                toolbarOffsetHeightPx.value = newOffset.coerceIn(-toolbarHeightPx, 0f)
            // here's the catch: let's pretend we consumed 0 in any case, since we want
            // LazyColumn to scroll anyway for good UX
            // We're basically watching scroll without taking it
//                return Offset.Zero
//            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
        ) {
//            item {
//                Text(
//                    text = "Texts"
//                )
//            }
        }
        OngeziComposeBottomBar()
    }
}

@Composable
fun OngeziComposeBottomBar() {
    var message by remember { mutableStateOf("") }
    Surface(
        elevation = 5.dp
    ) {
        Column(
            modifier = Modifier
                .absolutePadding(16.dp, 8.dp, 16.dp, 8.dp)
//            .requiredHeightIn(Dp.Unspecified, 181.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(shape = RoundedCornerShape(4.dp), color = Color(0xFFF8F7F7))
                    .fillMaxWidth()
                    .defaultMinSize(34.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                BasicTextField(
                    value = message,
                    onValueChange = {
                        message = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    maxLines = 4,
                    singleLine = false
                )
                if (message.isBlank()) {
                    Text(
                        text = "Andika hapa...",
                        fontWeight = FontWeight(200),
                        color = Color(0xFF7D7D7D),
                        fontSize = 13.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .absolutePadding(8.dp),
                    )
                }
            }
            Row {
//                IconButton(onClick = { }) {
//                    Icon(Icons.Outlined.Face, contentDescription = "tuma emoj")
//                }
                Spacer(Modifier.weight(1.0f))
                IconButton(
                    onClick = { },
                    enabled = message.isNotBlank()
                ) {
                    Icon(Icons.Sharp.Send, contentDescription = "tuma maneno")
                }
            }
        }
    }

}









