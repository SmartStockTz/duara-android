package com.fahamutech.duara.pages

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fahamutech.duara.components.MaongeziEmpty
import com.fahamutech.duara.components.MaongeziList
import com.fahamutech.duara.components.MaongeziMapyaFAB
import com.fahamutech.duara.components.MaongeziTopBar
import com.fahamutech.duara.models.Maongezi
import com.fahamutech.duara.models.UserModel
import com.fahamutech.duara.services.DuaraStorage
import com.fahamutech.duara.states.MaongeziState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalFoundationApi
@Composable
fun MaongeziPage(
    maongeziState: MaongeziState = viewModel(),
    navController: NavController,
    context: Context
) {
    val scope = rememberCoroutineScope()
    val maongezi = remember { mutableStateOf<List<Maongezi>?>(null) }
    var user: UserModel? by remember { mutableStateOf(null) }
    if (user != null) {
        Scaffold(
            topBar = {
                MaongeziTopBar(context, navController)
            },
            floatingActionButton = {
                MaongeziMapyaFAB(navController)
            },
            content = {
                if (maongezi.value != null) {
                    if (maongezi.value!!.isEmpty()) {
//                        Log.e("HAMNA MAONGEZI", "**********'")
                        MaongeziEmpty()
                    }
                    if (maongezi.value!!.isNotEmpty()) {
//                        Log.e("YAPO MAONGEZI", "**********'")
                        MaongeziList(maongezi.value!!, maongeziState, navController, context)
                    }
                }
            }
        )
    }
    DisposableEffect("maongezi-page") {
        val storage = DuaraStorage.getInstance(context)
        val s = scope.launch {
            user = storage.user().getUser()
            if (user == null) {
                navController.navigate("jiunge") {
                    popUpTo(0) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            } else {
                storage.maongezi().getMaongezi().collect {
                    maongezi.value = it
                }
            }
        }
        onDispose {
            s.cancel()
        }
    }
}