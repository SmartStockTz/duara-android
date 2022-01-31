package com.fahamutech.duaracore.pages

import android.content.Context
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.fahamutech.duaracore.components.OngeziBody
import com.fahamutech.duaracore.components.OngeziTopBar
import com.fahamutech.duaracore.models.Maongezi
import com.fahamutech.duaracore.models.PresenceResponse
import com.fahamutech.duaracore.models.UserModel
import com.fahamutech.duaracore.services.DuaraStorage
import com.fahamutech.duaracore.services.PresenceSocket
import com.fahamutech.duaracore.states.OngeziState
import com.fahamutech.duaracore.utils.messageToApp
import com.google.gson.Gson
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.launch
import org.json.JSONObject

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
    var status by remember { mutableStateOf("") }
    var socket by remember {
        mutableStateOf<Socket?>(null)
    }
    if (ongezi != null && user !== null) {
        Scaffold(
            topBar = { OngeziTopBar(ongezi!!, ongeziState, context, navController, status) },
            content = {
                OngeziBody(ongeziState, ongezi!!, user!!, context, socket)
            }
        )
    }
    DisposableEffect(ongezi?.id) {
        val listener = Emitter.Listener {
            val presenceResponse =
                Gson().fromJson(it[0].toString(), PresenceResponse::class.java)
            if (presenceResponse.body?.change?.snapshot != null) {
                val st = presenceResponse?.body?.change?.snapshot?.get("status")
                status = st?.toString() ?: ""
            }
        }
        val storage = DuaraStorage.getInstance(context)
        scope.launch {
            val uDao = storage.user()
            val maongeziDao = storage.maongezi()
            user = uDao.getUser()
            val a = maongeziDao.getOngeziInStore(id ?: "na")
            if (a != null && user !== null) {
                ongezi = a
                ongeziState.fetchMessage(ongezi?.id ?: "na", context)
                storage.message().markAllRead(ongezi?.id ?: "na")
                socket = PresenceSocket.start(context, connect = {
                    val gson = JSONObject()
                    val gsonBody = JSONObject()
                    gsonBody.put("s_x", user?.pub?.x)
                    gsonBody.put("r_x", ongezi?.receiver_pubkey?.x)
                    gsonBody.put("type", "p")
                    gson.put("body", gsonBody)
                    socket?.on("/presence", listener)
                    socket?.emit("/presence", gson)
                })
            } else {
                navController.popBackStack()
                messageToApp("Imeshindwa jua unaetaka ongea nae", context)
            }
        }
        onDispose {
            ongeziState.dispose(ongezi?.id ?: "na", context)
            socket?.disconnect()
            socket?.off("/presence", listener)
        }
    }
}








