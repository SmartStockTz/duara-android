package com.fahamutech.duaracore.pages

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fahamutech.duaracore.components.*
import com.fahamutech.duaracore.models.UserModel
import com.fahamutech.duaracore.services.DuaraStorage
import com.fahamutech.duaracore.states.MaduaraState
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
    var hasPermission by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        hasPermission = it
    }
    Scaffold(
        topBar = { MaduaraTopBar(maduaraState, context, navController) },
        content = {
            if (hasPermission) {
                if (maduara.isEmpty().and(syncsProgress == false)) {
                    UpoMwenyeweDialog(maduaraState, context)
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                    ) {
                        if (maduara.isNotEmpty()) {
                            HelperMessage()
                        }
                        MaduaraList(maduara, navController, context)
                    }
                }
            } else {
                when (PackageManager.PERMISSION_GRANTED) {
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_CONTACTS
                    ) -> {
                        hasPermission = true
                    }
                    else -> {
                        permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                    }
                }
            }
        }
    )
    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            maduaraState.fetchMaduara(context)
        }
    }
}
