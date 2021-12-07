package com.fahamutech.duara.components

import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DuaraTitle() {
    Text(
        text = "Duara,",
        modifier = Modifier.absolutePadding(40.dp, 24.dp, 0.dp, 0.dp),
        fontSize = 36.sp,
        fontWeight = FontWeight(500),
        lineHeight = 53.sp,
        fontStyle = FontStyle.Normal
    )
}