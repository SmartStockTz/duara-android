package com.fahamutech.duaracore.components

import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DuaraMtoaHudumaWelcomeText(){
    Text(
        text = "Karibu jaza barua pepe yako na nywila, uliyopewa wakati unafungua akaunti.",
        modifier = Modifier.absolutePadding(24.dp,24.dp,24.dp,16.dp),
        fontWeight = FontWeight(300),
        fontSize = 16.sp,
        lineHeight = 18.sp,
        color = Color(0xFF989898)
    )
}



