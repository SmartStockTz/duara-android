package com.fahamutech.duaracore.services

import android.content.Context
import android.util.Log
import com.fahamutech.duaracore.R
import com.fahamutech.duaracore.models.UserModel
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import java.net.URI


object PresenceSocket {
    @Synchronized
    fun start(context: Context, connect: (socket: Socket) -> Unit = {}, disconnect: () -> Unit = {}): Socket {
        val baseUrl = context.resources.getString(R.string.base_server_url) + "/presence"
        val uri: URI = URI.create(baseUrl)
//        val options = IO.Options.builder()
//            .setReconnection(true)
//            .build()
        val socket = IO.socket(uri)
        socket.on(Socket.EVENT_CONNECT) {
            Log.e("EVENTS", "connected*****")
            connect(socket)
        }
        socket.on(Socket.EVENT_DISCONNECT) {
            Log.e("EVENTS", "disconnected*****")
            disconnect()
        }
        socket.on(Socket.EVENT_CONNECT_ERROR) {
            Log.e("EVENTS ERR", it[0].toString())
        }
        socket.connect()
        return socket!!
    }
}

fun onlineStatus(user: UserModel?,context: Context): Socket {
    return PresenceSocket.start(context, connect = {
        val gson = JSONObject()
        val gsonBody = JSONObject()
        gsonBody.put("s_x", user?.pub?.x)
        gsonBody.put("r_x", Math.random().toString().replace(".", ""))
        gsonBody.put("type", "o")
        gson.put("body", gsonBody)
        it.emit("/presence", gson)
    })
}
