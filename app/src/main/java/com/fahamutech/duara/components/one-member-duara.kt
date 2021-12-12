package com.fahamutech.duara.components

import android.content.Context
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.fahamutech.duara.states.MaduaraState
import com.fahamutech.duara.utils.shareApp

@Composable
fun UpoMwenyeweDialog(maduaraState: MaduaraState, context: Context) {
    val showDialog by maduaraState.showOneMemberDialog.observeAsState()
    if (showDialog == true) {
        AlertDialog(
            onDismissRequest = {
                maduaraState.toggleShowOneMemberDialog(false)
            },
            title = {
                Text("Habari")
            },
            text = {
                Text(
                    "Upo mwenyewe kwenye ili duara, alika marafiki " +
                            "ili uweze kuwaona, au vuta tena data"
                )
            },
            confirmButton = {
                Button(onClick = {
                    shareApp(context)
                    maduaraState.toggleShowOneMemberDialog(false)
                }) {
                    Text(text = "Alika")
                }
            },
            dismissButton = {
                Button(onClick = {
                    maduaraState.syncMaduara(context)
                    maduaraState.toggleShowOneMemberDialog(false)
                }) {
                    Text(text = "Vuta")
                }
            }
        )
    }
}

