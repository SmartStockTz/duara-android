package com.fahamutech.duara.pages

import android.content.Context
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.fahamutech.duara.components.OngeziBody
import com.fahamutech.duara.components.OngeziTopBar
import com.fahamutech.duara.models.Ongezi
import com.fahamutech.duara.models.UserModel
import com.fahamutech.duara.services.getOngeziInStore
import com.fahamutech.duara.services.getUser
import com.fahamutech.duara.states.OngeziState
import com.fahamutech.duara.utils.messageToApp
import kotlinx.coroutines.launch

@Composable
fun OngeziPage(
    id: String?,
    ongeziState: OngeziState,
    navController: NavController,
    context: Context
) {
    val scope = rememberCoroutineScope()
    var ongezi by remember { mutableStateOf<Ongezi?>(null) }
    var user by remember { mutableStateOf<UserModel?>(null) }
    if (ongezi != null && user !== null) {
        Scaffold(
            topBar = { OngeziTopBar(ongezi!!, ongeziState, context, navController) },
            content = {
                OngeziBody(ongeziState, ongezi!!, user!!, context)
            }
        )
    }
    LaunchedEffect(id) {
        scope.launch {
            user = getUser()
            val a = getOngeziInStore(id ?: "na")
            if (a != null && user !== null) {
                ongezi = a
            } else {
                navController.popBackStack()
                messageToApp("Imeshindwa jua unaetaka ongea nae", context)
            }
        }
    }
}








