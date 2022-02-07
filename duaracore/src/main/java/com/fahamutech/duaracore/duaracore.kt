package com.fahamutech.duaracore

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.fahamutech.duaracore.pages.*
import com.fahamutech.duaracore.services.DuaraStorage
import com.fahamutech.duaracore.services.onlineStatus
import com.fahamutech.duaracore.services.syncContacts
import com.fahamutech.duaracore.states.OngeziState
import com.fahamutech.duaracore.utils.OPTIONS
import com.fahamutech.duaracore.utils.withTryCatch
import com.fahamutech.duaracore.workers.startPeriodicalRetrieveMessageWorker
import com.fahamutech.duaracore.workers.startPeriodicalSendMessageWorker
import com.google.android.gms.common.GoogleApiAvailability
import io.socket.client.Socket
import kotlinx.coroutines.launch

abstract class DuaraCoreActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this)
        startPeriodicalSendMessageWorker(this)
        startPeriodicalRetrieveMessageWorker(this)
//        val ongeziState by viewModels<OngeziState>()
//        setContent { DuaraApp(this, ongeziState) }
    }

    override fun onResume() {
        super.onResume()
        OPTIONS.IS_VISIBLE = true
    }

    override fun onPause() {
        super.onPause()
        OPTIONS.IS_VISIBLE = false
    }
}


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun DuaraCore(
    activity: ComponentActivity,
    ongeziState: OngeziState,
    hudumaList: @Composable () -> Unit = {},
    onInit: () -> Unit = {}
) {
    onInit()
    val context = LocalContext.current
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    NavHost(navController = navController, startDestination = "maongezi") {
        composable("jiunge") {
            JiungePage(
                context = activity,
                navController = navController
            )
        }
        composable("jiunge-mtoa-huduma") {
            JiungeMtoaHudumaPage(
                context = activity,
                navController = navController
            )
        }
        composable("maongezi") {
            MaongeziPage(
                navController = navController,
                context = activity,
                hudumaList = hudumaList
            )
        }
        composable(
            "maongezi/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType }),
            deepLinks = listOf(navDeepLink {
                uriPattern = "https://duaratz.web.app/maongezi/{id}"
            })
        ) {
            OngeziPage(
                id = it.arguments?.getString("id"),
                navController = navController,
                context = activity,
                ongeziState = ongeziState
            )
        }
        composable("maduara") {
            MaduaraPage(
                navController = navController,
                context = activity
            )
        }
        composable("ukurasa") {
            UkurasaPage(
                navController = navController,
                activity = activity
            )
        }
    }
    DisposableEffect("duara_app") {
        var socket: Socket? = null
        scope.launch {
            val storage = DuaraStorage.getInstance(context)
            val user = storage.user().getUser()
            socket = onlineStatus(user, context)
            withTryCatch(run = {
                syncContacts(activity)
            }) {
                Log.e("SYNCS ON INIT", it)
            }
        }
        onDispose {
            socket?.disconnect()
            socket?.off()
        }
    }
}
