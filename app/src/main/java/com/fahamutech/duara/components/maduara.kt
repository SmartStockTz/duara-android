package com.fahamutech.duara.components

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.sharp.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fahamutech.duara.states.MaduaraState

@Composable
fun MaduaraTopBar(
    maduaraState: MaduaraState, context: Context, navController: NavController
) {
    val syncMaduaraProgress by maduaraState.maduaraSyncProgress.observeAsState()
    TopAppBar {
        IconButton(onClick = {
            navController.popBackStack()
        }) {
            Icon(Icons.Sharp.ArrowBack, "back")
        }
        Text(
            text = "Maduara",
            fontSize = 24.sp,
            fontWeight = FontWeight(500),
            lineHeight = 36.sp,
            modifier = Modifier.absolutePadding(16.dp)
        )
        Box {
            if (syncMaduaraProgress == true) {
                CircularProgressIndicator()
            } else {
                IconButton(onClick = {
//                    maduaraState.syncMaduara(context)
                    maduaraState.syncMaduara(context)
                }) {
                    Icon(Icons.Default.Refresh, contentDescription = "vuta")
                }
            }
        }
    }
}