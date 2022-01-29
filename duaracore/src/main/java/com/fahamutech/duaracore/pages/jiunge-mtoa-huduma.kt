package com.fahamutech.duaracore.pages

import android.app.Activity
import android.content.Context
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    if (user == null) {
        Scaffold(
            topBar = {
                TopAppBar {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Sharp.ArrowBack, "back")
                    }
                    Text(
                        text = "Mtoa huduma",
                        fontSize = 24.sp,
                        fontWeight = FontWeight(500),
                        lineHeight = 36.sp,
                        modifier = Modifier.absolutePadding(16.dp)
                    )
                    Spacer(modifier = Modifier.weight(1.0f))
                }
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    DuaraMtoaHudumaWelcomeText()
                    NicknameInput(jiungeState)
                    PasswordInput(jiungeState)
                    JiungeMtoaHudumaButton(jiungeState, navController, context)
                }
            }
        )
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




