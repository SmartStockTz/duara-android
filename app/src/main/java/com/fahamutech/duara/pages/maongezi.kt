package com.fahamutech.duara.pages

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavController
import com.fahamutech.duara.components.HamnaMaongezi
import com.fahamutech.duara.components.ListYaMaongeziYote
import com.fahamutech.duara.components.MaongeziMapyaFAB
import com.fahamutech.duara.components.MaongeziTopBar
import com.fahamutech.duara.states.JiungeState
import com.fahamutech.duara.states.MaongeziState

@Composable
fun Maongezi(
    maongeziState: MaongeziState,
    jiungeState: JiungeState,
    navController: NavController,
) {
    val maongezi by maongeziState.maongezi.observeAsState()
    val user by jiungeState.user.observeAsState()
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
    LaunchedEffect(user != null) {
        jiungeState.loadUser()
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