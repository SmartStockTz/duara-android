package com.fahamutech.duaracore.components

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fahamutech.duaracore.states.MaduaraState
import com.fahamutech.duaracore.utils.shareApp

@Composable
fun UpoMwenyeweDialog(maduaraState: MaduaraState, context: Context) {
    Column(
        modifier = Modifier
            .absolutePadding(16.dp, 8.dp, 16.dp, 8.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "Hamna watoa huduma waliopo kwa sasa, angalia mtandao wako wa" +
                    " intenet kisha jaribu tena.",
            fontWeight = FontWeight(300),
            fontSize = 14.sp,
            modifier = Modifier.fillMaxWidth(),
            lineHeight = 20.sp
        )
        Row {
            TextButton(onClick = {
                maduaraState.syncMaduara(context)
            }) {
                Text(text = "Jaribu tena")
            }
//            TextButton(onClick = {
//                shareApp(context)
//            }) {
//                Text(text = "Alika marafiki")
//            }
        }
    }
}

@Preview
@Composable
fun Upo(){
    UpoMwenyeweDialog(
        maduaraState = viewModel(),
        context = LocalContext.current )
}

