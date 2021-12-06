package com.fahamutech.duara.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun JiungeButton() {
    Box(
        modifier = Modifier
            .absolutePadding(40.dp, 16.dp, 24.dp, 100.dp)
            .fillMaxWidth()
    ) {
        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                "Jiunge.",
                modifier = Modifier.fillMaxWidth(),
                style = TextStyle(textAlign = TextAlign.Start),
                fontWeight = FontWeight(500),
                lineHeight = 19.sp,
                fontSize = 16.sp
            )
        }
    }
}