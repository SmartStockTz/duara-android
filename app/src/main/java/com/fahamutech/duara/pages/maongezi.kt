package com.fahamutech.duara.pages

import android.content.Context
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fahamutech.duara.components.HamnaMaongezi
import com.fahamutech.duara.components.ListYaMaongeziYote
import com.fahamutech.duara.components.MaongeziMapyaFAB
import com.fahamutech.duara.components.MaongeziTopBar
import com.fahamutech.duara.models.UserModel
import com.fahamutech.duara.services.DuaraStorage
import com.fahamutech.duara.states.MaongeziState
import kotlinx.coroutines.launch

@ExperimentalFoundationApi
@Composable
fun MaongeziPage(
    maongeziState: MaongeziState = viewModel(),
    navController: NavController,
    context: Context
) {
    val scope = rememberCoroutineScope()
    val maongezi by maongeziState.maongezi.observeAsState()
    var user: UserModel? by remember { mutableStateOf(null) }
    if (user != null) {
        Scaffold(
            topBar = {
                MaongeziTopBar()
            },
            floatingActionButton = {
                MaongeziMapyaFAB(navController)
            },
            content = {
                if (maongezi != null) {
                    if (maongezi!!.isEmpty()) {
//                        Log.e("HAMNA MAONGEZI", "**********'")
                        HamnaMaongezi()
                    }
                    if (maongezi!!.isNotEmpty()) {
//                        Log.e("YAPO MAONGEZI", "**********'")
                        ListYaMaongeziYote(maongezi!!, maongeziState, navController, context)
                    }
                } else {
                    Log.e("NULL MAONGEZI", "**********'")
                }
            }
        )
    }
    LaunchedEffect("maongezi-page") {
        scope.launch {
            val uDao = DuaraStorage.getInstance(context).user()
            user = uDao.getUser()
            if (user == null) {
                navController.navigate("jiunge") {
                    popUpTo(0) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            } else {
                maongeziState.fetchMaongezi(context)
            }
        }
    }
}