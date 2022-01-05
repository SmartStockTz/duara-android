package com.fahamutech.duaracore.components

import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.fahamutech.duaracore.R

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
        Icon(painterResource(id = R.drawable.ic_baseline_chat_24), contentDescription = "maongezi mapya")
    }
}
