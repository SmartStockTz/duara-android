package com.fahamutech.duara

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.fahamutech.duara.ui.theme.DuaraTheme
import com.fahamutech.duaracore.DuaraCore
import com.fahamutech.duaracore.DuaraCoreActivity
import com.fahamutech.duaracore.states.OngeziState

class DuaraAppActivity : DuaraCoreActivity() {

    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ongeziState by viewModels<OngeziState>()
        setContent { DuaraApp(this, ongeziState) }
    }
}


@ExperimentalFoundationApi
@Composable
fun DuaraApp(
    activity: ComponentActivity,
    ongeziState: OngeziState,
) {
    DuaraTheme {
        Surface(color = MaterialTheme.colors.background) {
            DuaraCore(activity, ongeziState)
        }
    }
}
