package com.fahamutech.duara

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fahamutech.duara.models.UserModel
import com.fahamutech.duara.pages.JiungePage
import com.fahamutech.duara.pages.Maduara
import com.fahamutech.duara.pages.Maongezi
import com.fahamutech.duara.services.getUser
import com.fahamutech.duara.services.initLocalDatabase
import com.fahamutech.duara.states.JiungeState
import com.fahamutech.duara.states.MaduaraState
import com.fahamutech.duara.states.MaongeziState
import com.fahamutech.duara.ui.theme.DuaraTheme

class DuaraApp : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLocalDatabase(this)
        val user = getUser()
        val jiungeState by viewModels<JiungeState>()
        val maongeziState by viewModels<MaongeziState>()
        val maduaraState by viewModels<MaduaraState>()
        maduaraState.fetchMaduara(this)
        setContent {
            DuaraApp(
                user = user,
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
    user: UserModel?,
    jiungeState: JiungeState = viewModel(),
    maongeziState: MaongeziState = viewModel(),
    maduaraState: MaduaraState = viewModel(),
    activity: ComponentActivity
) {
    val navController = rememberNavController()
    DuaraTheme {
        Surface(color = MaterialTheme.colors.background) {
            NavHost(navController = navController, startDestination = "jiunge") {
                composable("jiunge") {
                    AuthGuard(user, jiungeState, navController, activity) {
                        Maongezi(maongeziState, navController)
                    }
                }
                composable("maongezi") {
                    AuthGuard(user, jiungeState, navController, activity) {
                        Maongezi(maongeziState, navController)
                    }
                }
                composable("maduara") {
//                    maduaraState.fetchMaduara(activity)
                    AuthGuard(user, jiungeState, navController, activity) {
                        Maduara(maduaraState, navController, activity)
                    }
                }
            }
        }
    }
}

@Composable
fun AuthGuard(
    user: UserModel?,
    jiungeState: JiungeState,
    navController: NavController,
    activity: Activity,
    ok: @Composable () -> Unit
) {
    if (user === null) {
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