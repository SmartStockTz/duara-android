package com.fahamutech.duara.pages

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.fahamutech.duara.components.*
import com.fahamutech.duara.models.UserModel
import com.fahamutech.duara.services.getUser
import com.fahamutech.duara.states.JiungeState
import com.fahamutech.duara.states.MaduaraState

@Composable
fun Maduara(
    maduaraState: MaduaraState,
    navController: NavController,
    context: Context
) {
    val localNumbers by maduaraState.maduaraLocalGroupByInitial.observeAsState()
    var user: UserModel? by remember { mutableStateOf(null) }
    if (user != null) {
        Scaffold(
            topBar = { MaduaraTopBar(maduaraState, context, navController) },
            content = {
                if (localNumbers?.keys?.isEmpty() == true) {
                    Text("")
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                    ) {
                        HelperMessage()
                        MaduaraLocalList(localNumbers!!, maduaraState)
                    }
                }
            }
        )
        UpoMwenyeweDialog(maduaraState, context)
    }
    LaunchedEffect("maduara") {
        user = getUser()
        if (user == null) {
            navController.navigate("jiunge") {
                popUpTo(0) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        } else {
            maduaraState.fetchMaduara(context)
        }
    }
}