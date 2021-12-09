package com.fahamutech.duara.components

import android.app.Activity
import android.util.Log
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
import com.fahamutech.duara.states.JiungeState

@Composable
fun JiungeButton(jiungeState: JiungeState, navController: NavController, context: Activity) {
    val onFetching by jiungeState.getIdentityProgress.observeAsState()
    Box(
        modifier = Modifier
            .absolutePadding(40.dp, 16.dp, 24.dp, 100.dp)
            .fillMaxWidth()
    ) {
        Button(
            onClick = {
                jiungeState.jiunge(context) {
//                    Log.e("FUCKKK nav", "use****r")
                    navController.navigate("maongezi") {
//                        popUpTo("jiunge"){
//                            inclusive = true
//                        }
//                        launchSingleTop = true
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
                    "Jiunge.",
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