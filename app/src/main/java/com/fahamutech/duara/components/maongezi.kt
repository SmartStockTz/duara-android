package com.fahamutech.duara.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fahamutech.duara.R
import com.fahamutech.duara.ui.theme.DuaraGreen

@Composable
fun MaongeziTopBar() {
    TopAppBar {
        Text(
            text = "Maongezi",
            fontSize = 24.sp,
            fontWeight = FontWeight(500),
            lineHeight = 36.sp,
            modifier = Modifier.absolutePadding(16.dp)
        )
    }
}

@Composable
fun MaongeziMapyaFAB(navController: NavController) {
    FloatingActionButton(
        onClick = {
            navController.navigate("maduara") {
                launchSingleTop = true
            }
        },
        backgroundColor = DuaraGreen
    ) {
        Icon(Icons.Sharp.Add, contentDescription = "maongezi mapya")
    }
}

@Composable
fun ListYaMaongeziYote() {
    Text("Maongezi yote")
}

@Composable
fun HamnaMaongezi() {
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
                painter = painterResource(id = R.drawable.no_chat),
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






