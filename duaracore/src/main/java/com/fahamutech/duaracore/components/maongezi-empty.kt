package com.fahamutech.duaracore.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fahamutech.duaracore.R


@Composable
fun MaongeziEmpty() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.size(100.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_duara_logo),
                contentDescription = "hamna"
            )
        }
        Text(
            "Bado hauna maongezi na mtu, " +
                    "bofya apo chini kulia kuanzisha maongezi mapya.",
            modifier = Modifier.absolutePadding(24.dp, 0.dp, 24.dp, 0.dp),
            fontWeight = FontWeight(300),
            color = Color(0xFF989898),
            textAlign = TextAlign.Center
        )
    }
}