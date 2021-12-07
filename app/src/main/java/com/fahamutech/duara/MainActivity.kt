package com.fahamutech.duara

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fahamutech.duara.pages.JiungePage
import com.fahamutech.duara.pages.Maongezi
import com.fahamutech.duara.services.getUser
import com.fahamutech.duara.services.initLocalDatabase
import com.fahamutech.duara.states.JiungeState
import com.fahamutech.duara.ui.theme.DuaraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLocalDatabase(this)
        val jiungeState by viewModels<JiungeState>()
        setContent {
            DuaraApp(jiungeState = jiungeState, this)
        }
    }
}

@Composable
fun DuaraApp(jiungeState: JiungeState, activity: ComponentActivity) {
    val navController = rememberNavController()
    DuaraTheme {
        Surface(color = MaterialTheme.colors.background) {
            NavHost(navController = navController, startDestination = "jiunge") {
                composable("jiunge") {
                    val u = getUser()
                    if (u === null) {
                        JiungePage(jiungeState, activity, navController)
                    } else {
                        Maongezi()
                    }
                }
                composable("maongezi") { Maongezi() }
            }
        }
    }
}
//
//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    DuaraTheme {
//        Greeting(viewModel())
//    }
//}