package com.fahamutech.duara.components

import android.content.Context
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fahamutech.duara.models.Maongezi
import com.fahamutech.duara.models.Message
import com.fahamutech.duara.models.MessageStatus
import com.fahamutech.duara.services.DuaraStorage
import com.fahamutech.duara.states.MaongeziState
import com.fahamutech.duara.ui.theme.DuaraGreen
import com.fahamutech.duara.utils.timeAgo
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun MaongeziItem(
    maongezi: Maongezi,
    maongeziState: MaongeziState,
    navController: NavController,
    context: Context
) {
    val scope = rememberCoroutineScope()
    var message by remember { mutableStateOf<Message?>(null) }
    var totalUnread by remember { mutableStateOf<Int?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .absolutePadding(0.dp)
            .combinedClickable(
                onClick = {
                    navController.navigate("maongezi/${maongezi.id}") {
                        launchSingleTop = true
                    }
                },
                onLongClick = {
                    showDeleteDialog = true
                }
            ),
    ) {
        OngeziItemPicture(maongezi)
        Column(
            modifier = Modifier.absolutePadding(6.dp, 8.dp, 16.dp, 8.dp),
        ) {
            OngeziItemNameAndTime(maongezi)
            OngeziItemLastMessage(message, totalUnread)
        }
    }
    ShowDeleteConversationDialog(showDeleteDialog) {
        when (it) {
            "n" -> {
                maongeziState.futaOngezi(maongezi, context)
                showDeleteDialog = false
            }
            "h" -> {
                showDeleteDialog = false
            }
            "f" -> {
                showDeleteDialog = false
            }
        }
    }
    DisposableEffect(maongezi.id) {
        val storage = DuaraStorage.getInstance(context)
        val sc = scope.launch {
            storage.message().maongeziLastMessage(maongezi.id).distinctUntilChanged().collect {
                message = it
            }
        }
        val st = scope.launch {
            storage.message().maongeziUnreadMessage(maongezi.id).distinctUntilChanged().collect {
//                Log.e("TOTAL UN", it.toString())
                totalUnread = it
            }
        }
        onDispose {
            sc.cancel()
            st.cancel()
        }
    }
}

@Composable
private fun ShowDeleteConversationDialog(
    showDeleteDialog: Boolean, onClose: (action: String) -> Unit
) {
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { onClose("f") },
            text = {
                Text("Unataka futa maongezi haya?")
            },
            dismissButton = {
                Button(onClick = { onClose("h") }) {
                    Text(text = "Hapana")
                }
            },
            confirmButton = {
                Button(onClick = { onClose("n") }) {
                    Text(text = "Ndio")
                }
            }
        )
    }
}

@ExperimentalMaterialApi
@Composable
private fun OngeziItemLastMessage(message: Message?, totalUnread: Int?) {
    val text = message?.content ?: ""
    val fw = if (message?.status ?: "" == MessageStatus.UNREAD.toString()) {
        500
    } else 300
    Row {
        Text(
            text = text,
            fontWeight = FontWeight(fw),
            fontSize = 13.sp,
            color = Color(0xFF747272),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.absolutePadding(0.dp, 0.dp, 48.dp, 0.dp)
        )
        Spacer(modifier = Modifier.weight(1.0f))
        if (totalUnread != 0 && totalUnread != null) Box(
            modifier = Modifier
                .padding(4.dp)
                .defaultMinSize(20.dp)
                .clip(CircleShape)
                .background(DuaraGreen),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = totalUnread?.toString() ?: "",
                color = Color.White,
                fontWeight = FontWeight(400),
                fontSize = 14.sp,
                modifier = Modifier.absolutePadding(4.dp, 1.dp, 4.dp, 1.dp)
            )
        }
    }
}

@Composable
private fun OngeziItemNameAndTime(maongezi: Maongezi) {
    Row {
        Text(
            text = maongezi.receiver_nickname,
            fontWeight = FontWeight(500),
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.weight(1.0f))
        Text(
            text = timeAgo(maongezi.date),
            color = Color(0xFF747272),
            fontWeight = FontWeight(400),
            fontSize = 14.sp,
//                    lineHeight = 24.sp
        )
    }
}

@Composable
private fun OngeziItemPicture(maongezi: Maongezi) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.absolutePadding(16.dp, 8.dp, 0.dp, 8.dp)
    ) {
        Image(
            painter = painterResource(id = com.fahamutech.duara.R.drawable.ic_list_item_bg),
            contentDescription = "profile picture",
            modifier = Modifier.size(44.dp)
        )
        Text(
            text = maongezi.receiver_nickname[0].toString(),
            fontWeight = FontWeight(400),
            color = Color.White,
            fontSize = 16.sp
        )
    }
}