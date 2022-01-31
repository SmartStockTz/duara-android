package com.fahamutech.duaracore.pages

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fahamutech.duaracore.components.MaongeziEmpty
import com.fahamutech.duaracore.components.MaongeziList
import com.fahamutech.duaracore.components.MaongeziMapyaFAB
import com.fahamutech.duaracore.components.MaongeziTopBar
import com.fahamutech.duaracore.models.Maongezi
import com.fahamutech.duaracore.models.UserModel
import com.fahamutech.duaracore.services.DuaraStorage
import com.fahamutech.duaracore.services.onlineStatus
import com.fahamutech.duaracore.states.MaongeziState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalFoundationApi
@Composable
fun MaongeziPage(
    maongeziState: MaongeziState = viewModel(),
    navController: NavController,
    context: Context,
    hudumaList: @Composable () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val maongezi = remember { mutableStateOf<List<Maongezi>?>(null) }
    var user: UserModel? by remember { mutableStateOf(null) }
    if (user != null) {
        Scaffold(
            topBar = {
                MaongeziTopBar(context, navController)
            },
            floatingActionButton = {
                MaongeziMapyaFAB(navController)
            },
            content = {
                Column {
                    hudumaList()
                    if (maongezi.value != null) {
                        if (maongezi.value!!.isEmpty()) {
                            MaongeziEmpty()
                        }
                        if (maongezi.value!!.isNotEmpty()) {
                            MaongeziList(maongezi.value!!, maongeziState, navController, context)
                        }
                    }
                }
            }
        )
    }
    DisposableEffect("maongezi-page") {
        val storage = DuaraStorage.getInstance(context)
        val socket: io.socket.client.Socket? = null
        val s = scope.launch {
            user = storage.user().getUser()
            if (user == null) {
                navController.navigate("jiunge") {
                    popUpTo(0) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            } else {
                onlineStatus(user, context)
                storage.maongezi().getMaongezi().collect {
                    maongezi.value = it
                }
            }
        }
        onDispose {
            socket?.disconnect()
            socket?.off()
            s.cancel()
        }
    }
}