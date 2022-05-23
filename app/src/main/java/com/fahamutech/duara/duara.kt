package com.fahamutech.duara

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.fahamutech.duara.ui.theme.DuaraTheme
import com.fahamutech.duaracore.DuaraCore
import com.fahamutech.duaracore.DuaraCoreActivity
import com.fahamutech.duaracore.states.OngeziState

class DuaraAppActivity : DuaraCoreActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ongeziState by viewModels<OngeziState>()
        setContent { DuaraApp(this, ongeziState) }
    }
}


@Composable
fun DuaraApp(
    activity: ComponentActivity,
    ongeziState: OngeziState,
) {
    DuaraTheme {
        Surface(color = MaterialTheme.colors.background) {
            DuaraCore(
                activity,
                ongeziState,
//                maduaraPage = {
//                    Text(text = "Fuck yeaah!")
//                }
            )
        }
    }
}
