package com.fahamutech.duara.pages

import android.content.Context
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.fahamutech.duara.components.OngeziBody
import com.fahamutech.duara.components.OngeziComposeBottomBar
import com.fahamutech.duara.components.OngeziTopBar
import com.fahamutech.duara.models.Ongezi
import com.fahamutech.duara.services.getOngeziInStore
import com.fahamutech.duara.states.OngeziState
import com.fahamutech.duara.utils.messageToApp
import kotlinx.coroutines.launch

@Composable
fun OngeziPage(
    id: String?, ongeziState: OngeziState,
    navController: NavController, context: Context
) {
    val scope = rememberCoroutineScope()
    var ongezi by remember { mutableStateOf<Ongezi?>(null) }
    if (ongezi != null) {
        Scaffold(
            topBar = { OngeziTopBar(ongezi!!, ongeziState, context, navController) },
            content = {
                OngeziBody()
            }
        )
    }
    LaunchedEffect(id) {
        scope.launch {
            val a = getOngeziInStore(id ?: "na")
            if (a != null) {
                ongezi = a
            } else {
                navController.popBackStack()
                messageToApp("Imeshindwa jua unaetaka ongea nae", context)
            }
        }
    }
}