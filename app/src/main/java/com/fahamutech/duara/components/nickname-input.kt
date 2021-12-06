package com.fahamutech.duara.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun NicknameInput() {
    var nickname by remember { mutableStateOf("") }
    Box(
        modifier = Modifier
            .absolutePadding(41.dp, 16.dp, 24.dp, 0.dp)
            .background(shape = RoundedCornerShape(4.dp), color = Color(0xFFF7F7F7))
            .fillMaxWidth()
            .height(40.dp)
    ) {
        BasicTextField(
            value = nickname,
            onValueChange = { nickname = it },
            modifier = Modifier
                .padding(9.dp)
                .fillMaxWidth()
                .fillMaxHeight(),
            maxLines = 1,
            singleLine = true
        )
    }
}