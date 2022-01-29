package com.fahamutech.duaracore.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fahamutech.duaracore.states.JiungeState

@Composable
fun PasswordInput(jiungeState: JiungeState) {
    val password by jiungeState.password.observeAsState("")
    Box(
        modifier = Modifier
            .absolutePadding(24.dp, 16.dp, 24.dp, 0.dp)
            .background(shape = RoundedCornerShape(4.dp), color = Color(0xFFF7F7F7))
            .fillMaxWidth()
            .height(40.dp)
    ) {
        BasicTextField(
            value = password,
            onValueChange = { jiungeState.onPasswordChange(it) },
            modifier = Modifier
                .padding(9.dp)
                .fillMaxWidth()
                .fillMaxHeight(),
            maxLines = 1,
            singleLine = true
        )
    }
}

private fun get_password(p: String): String {
    return p.ifBlank { "nywila" }
}








