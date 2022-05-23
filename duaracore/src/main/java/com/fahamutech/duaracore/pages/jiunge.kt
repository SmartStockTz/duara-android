package com.fahamutech.duaracore.pages

import android.app.Activity
import android.content.Context
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fahamutech.duaracore.components.*
import com.fahamutech.duaracore.models.UserModel
import com.fahamutech.duaracore.states.JiungeState

@Composable
fun JiungePage(
    jiungeState: JiungeState = viewModel(),
    context: Activity, navController: NavController
) {
    val user by jiungeState.user.observeAsState()
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    if (user == null) {
        val user1 by remember { mutableStateOf(UserModel()) }
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            UserImage(user1) { uri: Uri?, _: Context -> imageUri = uri }
            DuaraWelcomeText()
            NicknameInput(jiungeState, "Jina")
            AgeInput(jiungeState, "Umri")
            GenderInput(jiungeState, "Jinsia")
            JiungeButton(imageUri, jiungeState, navController, context)
            MtoaHudumaLink(navController)
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

@Composable
private fun MtoaHudumaLink(navController: NavController) {
    return ClickableText(
        text = AnnotatedString(
            "Ingia kama mtoa huduma"
        ),
        style = TextStyle(
            color = Color(0xFF005FCE),
            fontWeight = FontWeight(300),
            fontSize = 14.sp
        ),
        modifier = Modifier.absolutePadding(24.dp,0.dp,24.dp,100.dp)
    ) {
        navController.navigate("jiunge-mtoa-huduma") {
            launchSingleTop = true
        }
    }
}



