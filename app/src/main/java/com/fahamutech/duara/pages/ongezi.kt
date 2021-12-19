package com.fahamutech.duara.pages

import android.content.Context
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fahamutech.duara.components.OngeziBody
import com.fahamutech.duara.components.OngeziTopBar
import com.fahamutech.duara.models.Maongezi
import com.fahamutech.duara.models.UserModel
import com.fahamutech.duara.services.DuaraStorage
import com.fahamutech.duara.states.OngeziState
import com.fahamutech.duara.utils.messageToApp
import kotlinx.coroutines.launch

@Composable
fun OngeziPage(
    id: String?,
    ongeziState: OngeziState = viewModel(),
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
    DisposableEffect(id) {
        scope.launch {
            val storage = DuaraStorage.getInstance(context)
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
            ongeziState.dispose()
        }
    }
}








