package com.fahamutech.duara

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.fahamutech.duara.components.*
import com.fahamutech.duara.ui.theme.DuaraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DuaraTheme {
                Surface(color = MaterialTheme.colors.background) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
//    Text(text = "Hello $name!")
   Column(
       modifier = Modifier
           .fillMaxHeight()
           .fillMaxWidth()
           .verticalScroll(rememberScrollState())
   ) {
       Logo()
       DuaraTitle()
       DuaraWelcomeText()
       NicknameInput()
       JiungeButton()
   }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DuaraTheme {
        Greeting("Android")
    }
}