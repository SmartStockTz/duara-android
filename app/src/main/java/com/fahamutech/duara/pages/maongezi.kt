package com.fahamutech.duara.pages

import android.util.Log
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavController
import com.fahamutech.duara.components.HamnaMaongezi
import com.fahamutech.duara.components.ListYaMaongeziYote
import com.fahamutech.duara.components.MaongeziMapyaFAB
import com.fahamutech.duara.components.MaongeziTopBar
import com.fahamutech.duara.models.UserModel
import com.fahamutech.duara.services.getUser
import com.fahamutech.duara.states.JiungeState
import com.fahamutech.duara.states.MaongeziState

@Composable
fun Maongezi(
    maongeziState: MaongeziState,
    navController: NavController,
) {
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
                if (maongezi == null) {
                    HamnaMaongezi()
                }
                if (maongezi?.isEmpty() == true) {
                    HamnaMaongezi()
                }
                if (maongezi?.isNotEmpty() == true) {
                    ListYaMaongeziYote()
                }
            }
        )
    }
    LaunchedEffect("maongezi") {
        user = getUser()
        Log.e("JUGGG", user?.nickname?:"**********'")
        if (user == null) {
            navController.navigate("jiunge") {
                popUpTo(0) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }
}