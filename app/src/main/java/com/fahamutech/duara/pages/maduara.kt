package com.fahamutech.duara.pages

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fahamutech.duara.components.*
import com.fahamutech.duara.states.MaduaraState

@Composable
fun Maduara(
    maduaraState: MaduaraState,
    navController: NavController,
    context: Context
) {
    val localNumbers by maduaraState.maduaraLocal.observeAsState()
//    maduaraState.fetchMaduara(context)
    Scaffold(
        topBar = { MaduaraTopBar(maduaraState, context, navController) },
        content = {
            if (localNumbers?.isEmpty() == true) {
//                Text("Maduara yangu loading...")
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxHeight()
                        .fillMaxWidth(),
                ) {
                    for (i in localNumbers!!){
                        item(localNumbers!!.indexOf(i)) {
                            Column {
                                Text(i.name)
                                Text(i.normalizedNumber)
                            }
                        }
                    }
                }
            }
        }
    )
}