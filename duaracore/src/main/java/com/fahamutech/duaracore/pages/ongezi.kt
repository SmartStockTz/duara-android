package com.fahamutech.duaracore.pages

import android.content.Context
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.fahamutech.duaracore.components.OngeziBody
import com.fahamutech.duaracore.components.OngeziTopBar
import com.fahamutech.duaracore.models.Maongezi
import com.fahamutech.duaracore.models.UserModel
import com.fahamutech.duaracore.services.DuaraStorage
import com.fahamutech.duaracore.states.OngeziState
import com.fahamutech.duaracore.utils.messageToApp
import kotlinx.coroutines.launch

@Composable
fun OngeziPage(
    id: String?,
    ongeziState: OngeziState,
    navController: NavController,
    context: Context
) {
    val scope = rememberCoroutineScope()
    var ongezi by remember { mutableStateOf<Maongezi?>(null) }
    var user by remember { mutableStateOf<UserModel?>(null) }
    if (ongezi != null && user !== null) {
        Scaffold(
            topBar = { OngeziTopBar(ongezi!!, ongeziState, context, navController) },
            content = {
                OngeziBody(ongeziState, ongezi!!, user!!, context)
            }
        )
    }
    DisposableEffect(true) {
        val storage = DuaraStorage.getInstance(context)
        scope.launch {
            val uDao = storage.user()
            val maongeziDao = storage.maongezi()
            user = uDao.getUser()
            val a = maongeziDao.getOngeziInStore(id ?: "na")
            if (a != null && user !== null) {
                ongezi = a
                ongeziState.fetchMessage(ongezi?.id?:"na", context)
                storage.message().markAllRead(ongezi?.id?:"na")
            } else {
                navController.popBackStack()
                messageToApp("Imeshindwa jua unaetaka ongea nae", context)
            }
        }
        onDispose {
            ongeziState.dispose(ongezi?.id?:"na", context)
        }
    }
}








