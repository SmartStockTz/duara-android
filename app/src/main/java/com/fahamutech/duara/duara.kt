package com.fahamutech.duara

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fahamutech.duara.pages.JiungePage
import com.fahamutech.duara.pages.Maduara
import com.fahamutech.duara.pages.Maongezi
import com.fahamutech.duara.services.initLocalDatabase
import com.fahamutech.duara.states.JiungeState
import com.fahamutech.duara.states.MaduaraState
import com.fahamutech.duara.states.MaongeziState
import com.fahamutech.duara.ui.theme.DuaraTheme

class DuaraApp : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLocalDatabase(this)
        val jiungeState by viewModels<JiungeState>()
        val maongeziState by viewModels<MaongeziState>()
        val maduaraState by viewModels<MaduaraState>()
//        maduaraState.fetchMaduara(this)
        setContent {
            DuaraApp(
                jiungeState = jiungeState,
                maongeziState = maongeziState,
                maduaraState = maduaraState,
                this
            )
        }
    }
}

@Composable
fun DuaraApp(
    jiungeState: JiungeState = viewModel(),
    maongeziState: MaongeziState = viewModel(),
    maduaraState: MaduaraState = viewModel(),
    activity: ComponentActivity
) {
    val navController = rememberNavController()
    DuaraTheme {
        Surface(color = MaterialTheme.colors.background) {
            NavHost(navController = navController, startDestination = "maongezi") {
                composable("jiunge") {
                    JiungePage(jiungeState, activity, navController)
                }
                composable("maongezi") {
                    Maongezi(maongeziState, jiungeState, navController)
                }
                composable("maduara") {
                    Maduara(maduaraState, jiungeState, navController, activity)
                }
            }
        }
    }
}

//@Composable
//fun AuthGuard(
//    user: UserModel?,
//    jiungeState: JiungeState,
//    navController: NavController,
//    activity: Activity,
//    ok: @Composable () -> Unit
//) {
//    if (user === null) {
//        JiungePage(jiungeState, activity, navController)
//    } else {
//        ok()
//    }
//}

//
//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    DuaraTheme {
//        Greeting(viewModel())
//    }
//}