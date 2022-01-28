package com.fahamutech.duaracore.pages

import android.app.Activity
import android.content.Context
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fahamutech.duaracore.components.*
import com.fahamutech.duaracore.models.UserModel
import com.fahamutech.duaracore.states.JiungeState

@Composable
fun JiungeMtoaHudumaPage(
    jiungeState: JiungeState = viewModel(),
    context: Activity, navController: NavController
) {
    val user by jiungeState.user.observeAsState()
    var imageUri by remember { mutableStateOf<Uri?>(null)}
    if (user == null) {
        val user1 by remember { mutableStateOf(UserModel()) }
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            NicknameInput(jiungeState)
            PasswordInput(jiungeState)
            JiungeMtoaHudumaButton(imageUri, jiungeState, navController, context)
        }
    }
    LaunchedEffect("jiunge-mtoa-huduma-page") {
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




