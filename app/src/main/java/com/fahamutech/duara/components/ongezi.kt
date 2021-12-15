package com.fahamutech.duara.components

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.sharp.ArrowBack
import androidx.compose.material.icons.sharp.Send
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fahamutech.duara.R
import com.fahamutech.duara.models.*
import com.fahamutech.duara.states.OngeziState
import com.fahamutech.duara.utils.stringFromDate
import java.util.*

@Composable
fun OngeziTopBar(
    maongezi: Maongezi, ongeziState: OngeziState,
    context: Context, navController: NavController
) {
    var inSearchMode by remember { mutableStateOf(false) }
    Box {
        if (!inSearchMode) {
            OngeziTopBarNormal(maongezi, navController) {
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
    maongezi: Maongezi,
    navController: NavController, onSearchClick: () -> Unit
) {
    TopAppBar {
        IconButton(onClick = {
            navController.popBackStack()
        }) {
            Icon(Icons.Sharp.ArrowBack, "back")
        }
        Text(
            text = maongezi.receiver_nickname,
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
fun OngeziBody(
    ongeziState: OngeziState,
    maongezi: Maongezi,
    user: UserModel,
    context: Context
) {
    val messages by ongeziState.messages.observeAsState(initial = mutableListOf())
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {}
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    ) {
        OngeziMessageList(
            messages = messages,
            modifier = Modifier.weight(1f),
            user = user
        )
        OngeziComposeBottomBar(maongezi, ongeziState, user, context)
    }
    DisposableEffect(maongezi.id) {
        ongeziState.fetchMessage(maongezi.id, context)
        onDispose {
            ongeziState.resetMessages()
        }
    }
}

@Composable
fun OngeziMessageList(messages: List<Message>, user: UserModel, modifier: Modifier) {
    Log.e("***WOWOWMM", messages.size.toString())
    val state = rememberLazyListState()
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp, 0.dp),
        state = state,
        reverseLayout = true
    ) {
        messageByTime(messages).forEach { (date, m) ->
            messageInTimeGroupByOwner(m).forEach { (_, msg) ->
                items(msg) { message ->
                    val hO = msg.indexOf(message) != msg.size - 1
                    if (user.pub!!.x != message.sender_pubkey!!.x) {
                        MessageListItemReceive(hO, message)
                    }
                    if (user.pub!!.x == message.sender_pubkey!!.x) {
                        MessageListItemSent(hO, message)
                    }
                }
            }
            item {
                MessageListTimeStamp(date)
            }
        }
    }
}

fun messageInTimeGroupByOwner(
    messageList: List<Message>
): MutableMap<String, List<Message>> {
    return messageList.groupBy { m ->
        m.sender_nickname
    }.toMutableMap()
}

fun messageByTime(messages: List<Message>): MutableMap<String, List<Message>> {
    return messages.groupBy {
        val a = it.date.split(":").toMutableList()
        a.removeLastOrNull()
        a.joinToString("")
    }.toMutableMap()
}

@Composable
fun MessageListItemReceive(hideOwner: Boolean, message: Message) {
    Box(
        modifier = Modifier.absolutePadding(0.dp, 0.dp, 0.dp, 4.dp)
    ) {
        if (!hideOwner) {
            Image(
                painter = painterResource(id = R.drawable.ic_message_sender_bg),
                contentDescription = "picture",
                modifier = Modifier
                    .size(30.dp)
            )
        }
        Column(
            modifier = Modifier.absolutePadding(40.dp)
        ) {
            if (!hideOwner) {
                Text(
                    text = message.sender_nickname,
                    fontWeight = FontWeight(300),
                    fontSize = 14.sp,
                    lineHeight = 16.sp,
                    color = Color(0xFF747474),
                    modifier = Modifier.absolutePadding(0.dp, 0.dp, 0.dp, 4.dp)
                )
            }
            Text(text = message.content)
        }
    }
}

@Composable
fun MessageListItemSent(hideOwner: Boolean, message: Message) {
    Box(
        modifier = Modifier.absolutePadding(0.dp, 0.dp, 0.dp, 4.dp)
    ) {
        if (!hideOwner) {
            Image(
                painter = painterResource(id = R.drawable.ic_list_item_bg),
                contentDescription = "picture",
                modifier = Modifier
                    .size(30.dp)
            )
        }
        Column(
            modifier = Modifier.absolutePadding(40.dp)
        ) {
            if (!hideOwner) {
                Text(
                    text = message.sender_nickname,
                    fontWeight = FontWeight(300),
                    fontSize = 14.sp,
                    lineHeight = 16.sp,
                    color = Color(0xFF747474),
                    modifier = Modifier.absolutePadding(0.dp, 0.dp, 0.dp, 4.dp)
                )
            }
            Text(text = message.content)
        }
    }
}

@Composable
fun MessageListTimeStamp(date: String) {
    Text(
        text = date,
        fontSize = 14.sp,
        color = Color(0xFF3D3D3D),
        fontWeight = FontWeight(200),
        lineHeight = 16.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .absolutePadding(0.dp, 8.dp, 0.dp, 8.dp),
    )
}

@Composable
fun OngeziComposeBottomBar(
    maongezi: Maongezi, ongeziState: OngeziState, user: UserModel,
    context: Context,
) {
    var message by remember { mutableStateOf("") }
    Surface(
        elevation = 5.dp
    ) {
        Column(
            modifier = Modifier
                .absolutePadding(16.dp, 8.dp, 16.dp, 8.dp)
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
                    singleLine = false,
                    textStyle = TextStyle(
                        fontSize = 16.sp
                    )
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
                    onClick = {
                        sendMessage(maongezi, ongeziState, message, user, context)
                        message = ""
                    },
                    enabled = message.isNotBlank()
                ) {
                    Icon(Icons.Sharp.Send, contentDescription = "tuma maneno")
                }
            }
        }
    }

}

fun sendMessage(
    maongezi: Maongezi, ongeziState: OngeziState, message: String,
    userModel: UserModel, context: Context
) {
//    val date = stringFromDate(Date())
//    val messageLocal = Message(
//        date = date,
//        content = message,
//        duara_id = maongezi.receiver_duara_id,
//        receiver_pubkey = maongezi.receiver_pubkey,
//        sender_pubkey = userModel.pub,
//        sender_nickname = userModel.nickname,
//        receiver_nickname = maongezi.receiver_nickname,
//        maongezi_id = maongezi.id
//    )
    ongeziState.saveMessage(maongezi, message, userModel, context)
}









