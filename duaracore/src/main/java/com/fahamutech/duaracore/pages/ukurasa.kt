package com.fahamutech.duaracore.pages

import android.app.Activity
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.fahamutech.duaracore.components.DuaraBottomNav
import com.fahamutech.duaracore.components.UkurasaBody
import com.fahamutech.duaracore.components.UkurasaTopBar
import com.fahamutech.duaracore.models.UserModel
import com.fahamutech.duaracore.services.DuaraStorage
import com.fahamutech.duaracore.utils.messageToApp
import kotlinx.coroutines.launch

@Composable
fun UkurasaPage(activity: Activity, navController: NavController, route: String) {
    val scope = rememberCoroutineScope()
    var user by remember { mutableStateOf<UserModel?>(null) }
    if (user !== null) {
        Scaffold(
            topBar = { UkurasaTopBar(navController) },
            content = { UkurasaBody(user!!) },
            bottomBar = { DuaraBottomNav(navController)}
        )
    }
    LaunchedEffect("ukurasa_wangu") {
        scope.launch {
            val storage = DuaraStorage.getInstance(activity.applicationContext)
            val uDao = storage.user()
            user = uDao.getUser()
            if (user === null) {
                navController.popBackStack()
                messageToApp("Imeshindwa jua wewe ni nani", activity.applicationContext)
            }
        }
    }
}










