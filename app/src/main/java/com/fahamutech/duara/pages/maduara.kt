package com.fahamutech.duara.pages

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fahamutech.duara.components.HelperMessage
import com.fahamutech.duara.components.MaduaraList
import com.fahamutech.duara.components.MaduaraTopBar
import com.fahamutech.duara.components.UpoMwenyeweDialog
import com.fahamutech.duara.models.DuaraRemote
import com.fahamutech.duara.models.UserModel
import com.fahamutech.duara.services.DuaraStorage
import com.fahamutech.duara.states.MaduaraState
import kotlinx.coroutines.launch

@ExperimentalFoundationApi
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MaduaraPage(
    maduaraState: MaduaraState = viewModel(),
    navController: NavController,
    context: Context
) {
    val scope = rememberCoroutineScope()
    var user: UserModel? by remember { mutableStateOf(null) }
    if (user != null) {
        MaduaraView(maduaraState, navController, context)
    }
    LaunchedEffect("maduara") {
        scope.launch {
            val uDao = DuaraStorage.getInstance(context).user()
            user = uDao.getUser()
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
}

@ExperimentalMaterialApi
@Composable
fun MaduaraView(
    maduaraState: MaduaraState, navController: NavController, context: Context
) {
    val maduara by maduaraState.maduara.observeAsState(mutableListOf())
    val syncsProgress by maduaraState.maduaraSyncProgress.observeAsState(false)
    Scaffold(
        topBar = { MaduaraTopBar(maduaraState, context, navController) },
        content = {
            if (maduara.isEmpty().and(syncsProgress == false)) {
                UpoMwenyeweDialog(maduaraState, context)
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    if (maduara.isNotEmpty()){
                        HelperMessage()
                    }
                    MaduaraList(maduara, navController, context)
                }
            }
        }
    )
}
