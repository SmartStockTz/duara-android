package com.fahamutech.duara

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fahamutech.duara.pages.JiungePage
import com.fahamutech.duara.pages.Maduara
import com.fahamutech.duara.pages.Maongezi
import com.fahamutech.duara.services.getUser
import com.fahamutech.duara.services.initLocalDatabase
import com.fahamutech.duara.states.JiungeState
import com.fahamutech.duara.states.MaongeziState
import com.fahamutech.duara.ui.theme.DuaraTheme

class DuaraApp : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLocalDatabase(this)
        val jiungeState by viewModels<JiungeState>()
        val maongeziState by viewModels<MaongeziState>()
        setContent {
            DuaraApp(
                jiungeState = jiungeState,
                maongeziState = maongeziState,
                this
            )
        }
    }
}

@Composable
fun DuaraApp(jiungeState: JiungeState, maongeziState: MaongeziState, activity: ComponentActivity) {
    val navController = rememberNavController()
    DuaraTheme {
        Surface(color = MaterialTheme.colors.background) {
            NavHost(navController = navController, startDestination = "jiunge") {
                composable("jiunge") {
                    WithGuard(jiungeState, activity, navController) {
                        Maongezi(maongeziState, navController)
                    }
                }
                composable("maongezi") {
                    WithGuard(jiungeState, activity, navController) {
                        Maongezi(maongeziState, navController)
                    }
                }
                composable("maduara") {
                    WithGuard(jiungeState, activity, navController) {
                        Maduara()
                    }
                }
            }
        }
    }
}

@Composable
fun WithGuard(
    jiungeState: JiungeState,
    activity: ComponentActivity,
    navController: NavController,
    ok: @Composable () -> Unit
) {
    val u = getUser()
    if (u === null) {
        JiungePage(jiungeState, activity, navController)
    } else {
        ok()
    }
}
//
//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    DuaraTheme {
//        Greeting(viewModel())
//    }
//}