package com.fahamutech.duaracore.components

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fahamutech.duaracore.models.UserModel
import com.fahamutech.duaracore.workers.startUploadAndUpdateProfilePicture


@Composable
fun UkurasaBody(user: UserModel) {
    val ss = rememberScrollState()
    Column(
        modifier = Modifier
            .verticalScroll(ss)
            .absolutePadding(16.dp, 0.dp, 16.dp, 100.dp)
    ) {
        UserImage(user) { uri: Uri?, context: Context ->
            if (uri != null) {
                val cR = context.contentResolver
                val type = cR.getType(uri)
                val path = uri.toString()
                startUploadAndUpdateProfilePicture(path, type, context)
            }
        }
        Divider(color = Color(0xFFCCCCCC))
        UserName(user)
        Divider(color = Color(0xFFCCCCCC))
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun UserName(user: UserModel) {
    val focusRequester = remember {
        FocusRequester()
    }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var nickname by remember { mutableStateOf(user.nickname) }
    Row {
        Icon(
            Icons.Default.Person, contentDescription = "person",
            modifier = Modifier.absolutePadding(0.dp, 20.dp, 8.dp, 20.dp),
            tint = Color(0xFF6D6D6D)
        )
        Column(
            modifier = Modifier
                .weight(1.0f)
                .absolutePadding(0.dp, 10.dp, 0.dp, 10.dp)
        ) {
            Text(
                text = "Jina lako",
                fontSize = 12.sp,
                fontWeight = FontWeight(300),
                modifier = Modifier.absolutePadding(0.dp, 0.dp, 0.dp, 4.dp)
            )
            BasicTextField(
                value = nickname,
                onValueChange = {
                    nickname = it
                },
                textStyle = TextStyle(color = Color.Gray),
                enabled = false,
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .absolutePadding(0.dp, 0.dp, 16.dp, 0.dp),
                maxLines = 1,
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        updateName(nickname)
                        keyboardController?.hide()
                        focusManager.clearFocus(true)
                    }
                )
            )
        }
    }
}

private fun updateName(name: String) {

}





