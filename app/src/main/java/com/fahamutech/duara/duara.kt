package com.fahamutech.duara

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fahamutech.duara.pages.JiungePage
import com.fahamutech.duara.pages.Maduara
import com.fahamutech.duara.pages.Maongezi
import com.fahamutech.duara.pages.OngeziPage
import com.fahamutech.duara.services.initLocalDatabase
import com.fahamutech.duara.states.JiungeState
import com.fahamutech.duara.states.MaduaraState
import com.fahamutech.duara.states.MaongeziState
import com.fahamutech.duara.states.OngeziState
import com.fahamutech.duara.ui.theme.DuaraTheme
import com.fahamutech.duara.utils.generateKeyPair
import com.nimbusds.jose.crypto.bc.BouncyCastleProviderSingleton
import com.nimbusds.jose.jca.JCASupport
import java.security.Security

class DuaraApp : ComponentActivity() {

    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLocalDatabase(this)
        val jiungeState by viewModels<JiungeState>()
        val maongeziState by viewModels<MaongeziState>()
        val maduaraState by viewModels<MaduaraState>()
        val ongeziState by viewModels<OngeziState>()
        setContent {
            DuaraApp(
                jiungeState = jiungeState,
                maongeziState = maongeziState,
                maduaraState = maduaraState,
                ongeziState = ongeziState,
                this
            )
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun DuaraApp(
    jiungeState: JiungeState = viewModel(),
    maongeziState: MaongeziState = viewModel(),
    maduaraState: MaduaraState = viewModel(),
    ongeziState: OngeziState,
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
                    Maongezi(maongeziState, navController, activity)
                }
                composable(
                    "ongezi/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.StringType })
                ) {
                    OngeziPage(
                        it.arguments?.getString("id"),
                        ongeziState,
                        navController,
                        activity
                    )
                }
                composable("maduara") {
                    Maduara(maduaraState, navController, activity)
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