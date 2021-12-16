package com.fahamutech.duara.pages

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fahamutech.duara.components.*
import com.fahamutech.duara.states.JiungeState

@Composable
fun JiungePage(
    jiungeState: JiungeState = viewModel(),
    context: Activity, navController: NavController) {
    val user by jiungeState.user.observeAsState()
    if (user == null) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Logo()
            DuaraTitle()
            DuaraWelcomeText()
            NicknameInput(jiungeState)
            JiungeButton(jiungeState, navController, context)
        }
    }
    LaunchedEffect("jiunge-page") {
        jiungeState.loadUser(context)
        if (user != null) {
            navController.navigate("maongezi") {
                popUpTo(0) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }
}




