package com.fahamutech.duaracore.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fahamutech.duaracore.states.JiungeState

@Composable
fun NicknameInput(jiungeState: JiungeState) {
    val nickname by jiungeState.nickname.observeAsState("")
//    val nicknameFocus = FocusRequester()
    Box(
        modifier = Modifier
            .absolutePadding(41.dp, 16.dp, 24.dp, 0.dp)
            .background(shape = RoundedCornerShape(4.dp), color = Color(0xFFF7F7F7))
            .fillMaxWidth()
            .height(40.dp)
    ) {
        BasicTextField(
            value = nickname,
            onValueChange = { jiungeState.onNicknameChange(it) },
            modifier = Modifier
                .padding(9.dp)
                .fillMaxWidth()
//                .focusRequester(nicknameFocus)
                .fillMaxHeight(),
            maxLines = 1,
            singleLine = true
        )
    }
}