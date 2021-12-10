package com.fahamutech.duara.components

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.sharp.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fahamutech.duara.R
import com.fahamutech.duara.models.DuaraLocal
import com.fahamutech.duara.states.MaduaraState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

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
        Spacer(modifier = Modifier.weight(1.0f))
        Box(contentAlignment = Alignment.Center) {
            if (syncMaduaraProgress == true) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier
                        .absolutePadding(8.dp, 0.dp, 8.dp, 0.dp)
                        .size(24.dp)

                )
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


@Composable
fun HelperMessage() {
    Text(
        text = "Chagua duara kuona waliomo na uchague " +
                "wa kuongea nae.",
        fontWeight = FontWeight(300),
        fontSize = 14.sp,
        modifier = Modifier
            .absolutePadding(16.dp, 8.dp, 16.dp, 8.dp)
            .fillMaxWidth()
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MaduaraLocalList(grouped: Map<Char, List<DuaraLocal>>, maduaraState: MaduaraState) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        grouped.forEach { (initial, duaraLocalByInitial) ->
            stickyHeader {
                Text(initial.toString())
            }
            items(duaraLocalByInitial) { duaraLocal ->
                MaduaraLocalItem(duaraLocal, maduaraState)
            }
        }
    }
}

@Composable
fun MaduaraLocalItem(i: DuaraLocal, maduaraState: MaduaraState) {
    val waliomoKwenyeDuara by maduaraState.waliomoKwenyeDuara.observeAsState()
    var message = "Upo mwenyewe."
    if (waliomoKwenyeDuara!! > 1) {
        val w = waliomoKwenyeDuara!! - 1
        message = "Wewe na $w wengine."
    }
    Row(
        modifier = Modifier.absolutePadding(16.dp, 0.dp, 0.dp, 8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_list_item_bg),
                contentDescription = "logo"
            )
            Text(
                text = i.name[0].toString(),
                fontWeight = FontWeight(500),
                color = Color.White,
                fontSize = 24.sp
            )
        }
        Column(
            modifier = Modifier.absolutePadding(8.dp)
        ) {
            Text(
                text = i.name,
                fontWeight = FontWeight(500),
                fontSize = 16.sp
            )
            Text(
                text = i.normalizedNumber,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = Color(0xFF747272)
            )
            Text(
                text = message,
                fontWeight = FontWeight(200),
                fontSize = 14.sp,
                color = Color(0xFF747272)
            )
            LaunchedEffect(i.normalizedNumber) {
                maduaraState.fetchDuaraMembers(i.normalizedNumber)
            }
        }
    }
}