package com.fahamutech.duaracore.components

import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Add
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun MaongeziMapyaFAB(navController: NavController) {
    FloatingActionButton(
        onClick = {
            navController.navigate("maduara") {
                launchSingleTop = true
            }
        },
        backgroundColor = MaterialTheme.colors.primary
    ) {
        Icon(Icons.Sharp.Add, contentDescription = "maongezi mapya")
    }
}