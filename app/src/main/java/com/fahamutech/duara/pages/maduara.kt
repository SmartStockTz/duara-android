package com.fahamutech.duara.pages

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fahamutech.duara.R
import com.fahamutech.duara.components.*
import com.fahamutech.duara.states.JiungeState
import com.fahamutech.duara.states.MaduaraState

@Composable
fun Maduara(
    maduaraState: MaduaraState,
    jiungeState: JiungeState,
    navController: NavController,
    context: Context
) {
    val localNumbers by maduaraState.maduaraLocal.observeAsState()
    val user by jiungeState.user.observeAsState()
    if (user != null) {
        Scaffold(
            topBar = { MaduaraTopBar(maduaraState, context, navController) },
            content = {
                if (localNumbers?.keys?.isEmpty() == true) {
                    Text("Hauna namba za simu au haujaruhusu kusoma namba za simu")
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
    }
    LaunchedEffect("maduara") {
        maduaraState.fetchMaduara(context)
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