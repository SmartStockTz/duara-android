package com.fahamutech.duaracore.components

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.sharp.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fahamutech.duaracore.models.Maongezi
import com.fahamutech.duaracore.models.PresenceBody
import com.fahamutech.duaracore.models.PresenceResponse
import com.fahamutech.duaracore.services.DuaraStorage
import com.fahamutech.duaracore.services.PresenceSocket
import com.fahamutech.duaracore.services.syncContacts
import com.fahamutech.duaracore.states.OngeziState
import com.fahamutech.duaracore.utils.withTryCatch
import com.google.gson.Gson
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.launch
import org.json.JSONObject

@Composable
fun OngeziTopBar(
    maongezi: Maongezi, ongeziState: OngeziState,
    context: Context, navController: NavController,
    status: String
) {
//    var inSearchMode by remember { mutableStateOf(false) }
    Box {
//        if (!inSearchMode) {
        OngeziTopBarNormal(maongezi, navController, status) {
//                inSearchMode = true
        }
//        } else {
//            OngeziTopBarSearch(ongeziState, context) {
//                inSearchMode = false
//            }
//        }
    }
}

@Composable
private fun OngeziTopBarNormal(
    maongezi: Maongezi,
    navController: NavController,
    status: String,
    onSearchClick: () -> Unit
) {
    TopAppBar {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        IconButton(onClick = {
            navController.popBackStack()
        }) {
            Icon(Icons.Sharp.ArrowBack, "back")
        }
        Column {
            Text(
                text = maongezi.receiver_nickname,
                fontSize = 24.sp,
                fontWeight = FontWeight(500),
                lineHeight = 36.sp,
                modifier = Modifier.absolutePadding(16.dp)
            )
            Text(
                text = status,
                fontSize = 14.sp,
                fontWeight = FontWeight(300),
                modifier = Modifier.absolutePadding(16.dp)
            )
        }
        Spacer(modifier = Modifier.weight(1.0f))
//        IconButton(onClick = {
//            onSearchClick()
//        }) {
//            Icon(Icons.Default.Search, contentDescription = "tafuta")
//        }
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