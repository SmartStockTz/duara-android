package com.fahamutech.duaracore.components

import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fahamutech.duaracore.services.DuaraStorage
import kotlinx.coroutines.launch

@Composable
fun UkurasaTopBar(
    navController: NavController
) {
    TopAppBar(
        title = {
            Text(
                text = "Taarifa zako",
                fontSize = 24.sp,
                fontWeight = FontWeight(500),
                lineHeight = 36.sp,
                modifier = Modifier.absolutePadding(16.dp)
            )
        },
        actions = {
            ActionsView(navController)
        }
    )
}

@Composable
private fun ActionsView(navController: NavController) {
    val showMenu = remember { mutableStateOf(false) }
    IconButton(onClick = { showMenu.value = !showMenu.value }) {
        Icon(
            Icons.Default.MoreVert, contentDescription = "more_menu",
            tint = Color.White
        )
    }
    DropdownMenu(
        expanded = showMenu.value,
        onDismissRequest = { showMenu.value = false }
    ) {
        val scope = rememberCoroutineScope()
        val context = LocalContext.current
        DropdownMenuItem(onClick = {
            scope.launch {
                val storage = DuaraStorage.getInstance(context)
                storage.user().deleteAll()
                storage.maongezi().deleteAll()
                storage.maduara().deleteAll()
                storage.message().deleteAll()
                storage.messageCid().deleteAll()
                storage.messageOutbox().deleteAll()
                navController.navigate("jiunge") {
                    launchSingleTop = true
                    popUpTo(0){
                        inclusive = true
                    }
                }
            }
        }) {
            Text(text = "Badili akaunti")
        }
    }
}
