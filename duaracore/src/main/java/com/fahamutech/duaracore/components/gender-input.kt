package com.fahamutech.duaracore.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.fahamutech.duaracore.states.JiungeState

@Composable
fun GenderInput(jiungeState: JiungeState, placeholder: String) {
    val gender by jiungeState.gender.observeAsState("")
    var showChooseGender by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .absolutePadding(24.dp, 16.dp, 24.dp, 0.dp)
            .background(shape = RoundedCornerShape(4.dp), color = Color(0xFFF7F7F7))
            .fillMaxWidth()
            .height(40.dp)
            .clickable {
                showChooseGender = true
            }
    ) {
        val tM = Modifier
            .padding(9.dp)
            .fillMaxWidth()
            .fillMaxHeight()
        if (gender.isEmpty()) {
            Text(placeholder, style = TextStyle(color = Color.Gray), modifier = tM)
        } else {
            Text(text = gender, style = TextStyle(color = Color.Black), modifier = tM)
        }
    }
    DialogGender(
        show = showChooseGender,
        onDismiss = { showChooseGender = false },
        onAnswer = {
            jiungeState.onGenderChange(it)
        }
    )
}

@Composable
private fun DialogGender(show: Boolean, onDismiss: () -> Unit, onAnswer: (j: String) -> Unit) {
    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            text = {
                Column {
                    TextButton(onClick = {
                        onAnswer("me")
                        onDismiss()
                    }) {
                        Text("Mwanaume")
                    }
                    TextButton(onClick = {
                        onAnswer("ke")
                        onDismiss()
                    }) {
                        Text("Mwanamke")
                    }
                }
            },
            buttons = {}
        )
    }
}