package com.fahamutech.duara.pages

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
import com.fahamutech.duara.components.*
import com.fahamutech.duara.models.UserModel
import com.fahamutech.duara.states.JiungeState

@Composable
fun JiungePage(
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
            UserImage(user1) { uri: Uri?, context: Context ->
                imageUri = uri
            }
//            DuaraTitle()
            DuaraWelcomeText()
            NicknameInput(jiungeState)
            JiungeButton(imageUri, jiungeState, navController, context)
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




