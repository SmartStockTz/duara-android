package com.fahamutech.duara.pages

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.fahamutech.duara.components.*
import com.fahamutech.duara.states.JiungeState

@Composable
fun JiungePage(jiungeState: JiungeState, context: Activity, navController: NavController) {
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