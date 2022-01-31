package com.fahamutech.duaracore.components

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.fahamutech.duaracore.models.Maongezi
import com.fahamutech.duaracore.models.UserModel
import com.fahamutech.duaracore.states.OngeziState
import io.socket.client.Socket

@Composable
fun OngeziBody(
    ongeziState: OngeziState,
    maongezi: Maongezi,
    user: UserModel,
    context: Context,
    socket: Socket?
) {
    val messages by ongeziState.messages.observeAsState(initial = mutableListOf())
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {}
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    ) {
        OngeziMessageList(
            messages = messages,
            modifier = Modifier.weight(1f),
            user = user
        )
        OngeziComposeBottomBar(maongezi, ongeziState, user, context, socket)
    }
}
