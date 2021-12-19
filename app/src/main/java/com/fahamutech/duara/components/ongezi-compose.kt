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
    ongeziState.saveMessage(maongezi, message, userModel, context)
}






