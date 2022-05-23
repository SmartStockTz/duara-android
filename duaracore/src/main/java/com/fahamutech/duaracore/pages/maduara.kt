package com.fahamutech.duaracore.pages

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fahamutech.duaracore.R
import com.fahamutech.duaracore.components.*
import com.fahamutech.duaracore.models.UserModel
import com.fahamutech.duaracore.services.DuaraStorage
import com.fahamutech.duaracore.states.MaduaraState
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun MaduaraPage(
    maduaraState: MaduaraState = viewModel(),
    navController: NavController,
    context: Context,
    maduaraPage: @Composable() (() -> Unit)?
) {
    val scope = rememberCoroutineScope()
    var user: UserModel? by remember { mutableStateOf(null) }
    if (user != null) {
        MaduaraView(maduaraState, navController, context, maduaraPage)
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

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun MaduaraView(
    maduaraState: MaduaraState,
    navController: NavController,
    context: Context,
    maduaraPage: @Composable() (() -> Unit)?,
) {
    Scaffold(
        topBar = {
            if (maduaraPage == null) (MaduaraTopBar(maduaraState, context, navController))
        },
        bottomBar = { DuaraBottomNav(navController) },
        content = {
            if (maduaraPage != null) maduaraPage()
            else MaduaraPageOg(
                maduaraState = maduaraState,
                context = context,
                navController = navController
            )
        }
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
private fun MaduaraPageOg(
    maduaraState: MaduaraState,
    context: Context,
    navController: NavController
) {
    val maduara by maduaraState.maduara.observeAsState(mutableListOf())
    val syncsProgress by maduaraState.maduaraSyncProgress.observeAsState(false)
    var hasPermission by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        hasPermission = it
    }
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
        val maduaraSignatures = context.resources.getStringArray(R.array.maduara_signatures)
        if (maduaraSignatures.isNotEmpty()) {
            hasPermission = true
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
    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            maduaraState.fetchMaduara(context)
        }
    }
}
