package com.fahamutech.duara.components

import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DuaraWelcomeText(){
    Text(
        text = "Karibu jaza jina apo chini, kisha bofya neno jiunge.",
        modifier = Modifier.absolutePadding(40.dp,8.dp,24.dp,0.dp),
        fontWeight = FontWeight(300),
        fontSize = 16.sp,
        lineHeight = 18.sp,
        color = Color(0xFF989898)
    )
}