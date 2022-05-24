package com.fahamutech.duaracore.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.fahamutech.duaracore.R

@Composable
fun DuaraBottomNav(navController: NavController) {
    val isDarMode = isSystemInDarkTheme()
    val bgColor = if (!isDarMode) MaterialTheme.colors.primary else Color(0xFF00260F)
    BottomNavigation(
        backgroundColor = bgColor
    ) {
        BottomNavigationItem(
            selected = navController.currentDestination?.route === "maongezi",
            onClick = { navigate(navController, "maongezi") },
            icon = {
                Icon(
                    painterResource(id = R.drawable.ic_baseline_chat_24),
                    contentDescription = "mongezi"
                )
            }
        )
        BottomNavigationItem(
            selected = navController.currentDestination?.route === "maduara",
            onClick = { navigate(navController, "maduara") },
            icon = { Icon(painterResource(id = R.drawable.ic_baseline_group_work_24), contentDescription = "maduara") }
        )
        BottomNavigationItem(
            selected = navController.currentDestination?.route === "ukurasa",
            onClick = { navigate(navController, "ukurasa") },
            icon = { Icon(Icons.Default.Settings, contentDescription = "ukurasa") }
        )
    }
}

fun navigate(navController: NavController, route: String) {
    navController.navigate(route) {
        launchSingleTop = true
        popUpTo(0) {
            saveState = true
//            inclusive = true
        }
        restoreState = true
    }
}