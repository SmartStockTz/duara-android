package com.fahamutech.duara.components

import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Add
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.fahamutech.duara.ui.theme.DuaraGreen

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