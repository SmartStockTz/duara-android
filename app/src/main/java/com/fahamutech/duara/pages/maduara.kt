package com.fahamutech.duara.pages

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fahamutech.duara.components.*
import com.fahamutech.duara.models.DuaraRemote
import com.fahamutech.duara.models.UserModel
import com.fahamutech.duara.services.getUser
import com.fahamutech.duara.states.MaduaraState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@ExperimentalFoundationApi
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Maduara(
    maduaraState: MaduaraState,
    navController: NavController,
    context: Context
) {
    var duaraWaliomo by remember { mutableStateOf<List<DuaraRemote>>(mutableListOf()) }
    val scope = rememberCoroutineScope()
    var user: UserModel? by remember { mutableStateOf(null) }
    if (user != null) {
        val bottomState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
        ModalBottomSheetLayout(
            sheetState = bottomState,
            sheetContent = { MaduaraWaliomoSheetContent(duaraWaliomo, navController) },
            sheetShape = RoundedCornerShape(30.dp, 30.dp)
        ) {
            MaduaraView(maduaraState, navController, bottomState, scope, context) {
                duaraWaliomo = it
            }
        }
    }
    LaunchedEffect("maduara") {
        scope.launch {
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
}

@ExperimentalMaterialApi
@Composable
fun MaduaraView(
    maduaraState: MaduaraState,
    navController: NavController,
    bottomState: ModalBottomSheetState,
    scope: CoroutineScope,
    context: Context,
    onDuaraWaliomo: (List<DuaraRemote>) -> Unit
) {
    val localNumbers by maduaraState.maduaraLocalGroupByInitial.observeAsState()
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
                    MaduaraLocalList(localNumbers!!, maduaraState) {
                        scope.launch { bottomState.show() }
                        onDuaraWaliomo(it)
                    }
                }
            }
        }
    )
    UpoMwenyeweDialog(maduaraState, context)
}
