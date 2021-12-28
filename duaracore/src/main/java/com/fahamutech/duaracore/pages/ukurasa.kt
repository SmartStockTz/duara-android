package com.fahamutech.duaracore.pages

import android.content.Context
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.fahamutech.duaracore.components.UkurasaBody
import com.fahamutech.duaracore.components.UkurasaTopBar
import com.fahamutech.duaracore.models.UserModel
import com.fahamutech.duaracore.services.DuaraStorage
import com.fahamutech.duaracore.utils.messageToApp
import kotlinx.coroutines.launch

@Composable
fun UkurasaPage(context: Context, navController: NavController) {
    val scope = rememberCoroutineScope()
    var user by remember { mutableStateOf<UserModel?>(null) }
    if (user !== null) {
        Scaffold(
            topBar = { UkurasaTopBar(navController) },
            content = { UkurasaBody(user!!) }
        )
    }
    LaunchedEffect("ukurasa_wangu") {
        scope.launch {
            val storage = DuaraStorage.getInstance(context)
            val uDao = storage.user()
            user = uDao.getUser()
            if (user === null) {
                navController.popBackStack()
                messageToApp("Imeshindwa jua wewe ni nani", context)
            }
        }
    }
}










