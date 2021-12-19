package com.fahamutech.duara.components

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
import com.fahamutech.duara.models.Maongezi
import com.fahamutech.duara.models.UserModel
import com.fahamutech.duara.states.OngeziState

@Composable
fun OngeziBody(
    ongeziState: OngeziState,
    maongezi: Maongezi,
    user: UserModel,
    context: Context
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
        OngeziComposeBottomBar(maongezi, ongeziState, user, context)
    }
}
