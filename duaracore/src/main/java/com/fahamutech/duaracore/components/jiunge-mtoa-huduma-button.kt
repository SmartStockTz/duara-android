package com.fahamutech.duaracore.components

import android.app.Activity
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fahamutech.duaracore.states.JiungeState

@Composable
fun JiungeMtoaHudumaButton(
    imageUri: Uri?,
    jiungeState: JiungeState,
    navController: NavController,
    context: Activity
) {
    val onFetching by jiungeState.mtoaHudumaProgress.observeAsState()
    Box(
        modifier = Modifier
            .absolutePadding(24.dp, 16.dp, 24.dp, 16.dp)
            .fillMaxWidth()
    ) {
        Button(
            onClick = {
                jiungeState.jiungeMtoaHuduma(context) {
                    navController.navigate("maongezi") {
                        popUpTo(0) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !onFetching!!
        ) {
            if (onFetching as Boolean) {
                Text(
                    "Subiri...",
                    modifier = Modifier.fillMaxWidth(),
                    style = TextStyle(textAlign = TextAlign.Start),
                    fontWeight = FontWeight(500),
                    lineHeight = 19.sp,
                    fontSize = 16.sp
                )
            } else {
                Text(
                    "Ingia kama mtoa huduma.",
                    modifier = Modifier.fillMaxWidth(),
                    style = TextStyle(textAlign = TextAlign.Start),
                    fontWeight = FontWeight(500),
                    lineHeight = 19.sp,
                    fontSize = 16.sp
                )
            }
        }
    }
}