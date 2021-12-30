package com.fahamutech.duaracore.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fahamutech.duaracore.services.DuaraStorage
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@Composable
fun MaongeziTopBar(context: Context, navController: NavController) {
    val totalUnread = remember { mutableStateOf<Int?>(0) }
    val scope = rememberCoroutineScope()
    TopAppBar(
        title = {
            TitleView(totalUnread.value)
        },
        actions = {
            ActionsView(navController)
        }
    )
    DisposableEffect("maongezi_top_bar") {
        val storage = DuaraStorage.getInstance(context)
        val s = scope.launch {
            storage.message().totalUnread().distinctUntilChanged().collect {
                totalUnread.value = it
            }
        }
        onDispose {
            s.cancel()
        }
    }
}

@Composable
private fun ActionsView(navController: NavController) {
    val showMenu = remember { mutableStateOf(false) }
    IconButton(onClick = { showMenu.value = !showMenu.value }) {
        Icon(
            Icons.Default.MoreVert, contentDescription = "more_menu",
            tint = Color.White
        )
    }
    DropdownMenu(
        expanded = showMenu.value,
        onDismissRequest = { showMenu.value = false }
    ) {
        DropdownMenuItem(onClick = {
            navController.navigate("ukurasa"){
                launchSingleTop = true
            }
        }) {
            Text(text = "Picha & Jina")
        }
    }
}

@Composable
private fun TitleView(totalUnread: Int?) {
    Text(
        text = "Maongezi",
        fontSize = 24.sp,
        fontWeight = FontWeight(500),
        lineHeight = 36.sp,
        modifier = Modifier.absolutePadding(16.dp)
    )
    if (totalUnread != 0 && totalUnread != null) Box(
        modifier = Modifier
            .padding(4.dp)
            .defaultMinSize(20.dp)
            .clip(CircleShape)
            .background(Color.White),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = totalUnread.toString(),
            color = MaterialTheme.colors.primary,
            fontWeight = FontWeight(400),
            fontSize = 14.sp,
            modifier = Modifier.absolutePadding(4.dp, 1.dp, 4.dp, 1.dp)
        )
    }
}
