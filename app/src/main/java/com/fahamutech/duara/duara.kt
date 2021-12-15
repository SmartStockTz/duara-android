package com.fahamutech.duara

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fahamutech.duara.pages.JiungePage
import com.fahamutech.duara.pages.MaduaraPage
import com.fahamutech.duara.pages.MaongeziPage
import com.fahamutech.duara.pages.OngeziPage
import com.fahamutech.duara.ui.theme.DuaraTheme
import com.fahamutech.duara.workers.startPeriodicalRetrieveMessageWorker
import com.fahamutech.duara.workers.startPeriodicalSendMessageWorker
import com.google.android.gms.common.GoogleApiAvailability

class DuaraApp : ComponentActivity() {

    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this)
        startPeriodicalSendMessageWorker(this)
        startPeriodicalRetrieveMessageWorker(this)
        setContent { DuaraApp(this) }
    }
}

@ExperimentalFoundationApi
@Composable
fun DuaraApp(activity: ComponentActivity) {
    val navController = rememberNavController()
    DuaraTheme {
        Surface(color = MaterialTheme.colors.background) {
            NavHost(navController = navController, startDestination = "maongezi") {
                composable("jiunge") {
                    JiungePage(
                        context = activity,
                        navController = navController
                    )
                }
                composable("maongezi") {
                    MaongeziPage(
                        navController = navController,
                        context = activity
                    )
                }
                composable(
                    "ongezi/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.StringType })
                ) {
                    OngeziPage(
                        id = it.arguments?.getString("id"),
                        navController = navController,
                        context = activity
                    )
                }
                composable("maduara") {
                    MaduaraPage(
                        navController = navController,
                        context = activity
                    )
                }
            }
        }
    }
}
