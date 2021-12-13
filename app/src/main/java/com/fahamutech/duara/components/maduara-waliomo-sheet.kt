package com.fahamutech.duara.components

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fahamutech.duara.R
import com.fahamutech.duara.models.DuaraRemote
import com.fahamutech.duara.models.Ongezi
import com.fahamutech.duara.services.saveOngezi
import com.fahamutech.duara.utils.stringFromDate
import kotlinx.coroutines.launch
import java.util.*

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun MaduaraWaliomoSheetContent(duaraRemoteWaliomo: List<DuaraRemote>, navController: NavController) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
    ) {
        WaliomoKwenyeDuaraHeader()
        LazyVerticalGrid(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            cells = GridCells.Adaptive(minSize = 102.dp),
        ) {
            items(duaraRemoteWaliomo) { d ->
                DuaraMemberItem(d, navController)
            }
        }
    }
}

@Composable
fun WaliomoKwenyeDuaraHeader() {
    Text(
        text = "Chagua mtu kwenye ili duara wakuongea nae",
        fontSize = 16.sp,
        fontWeight = FontWeight(300),
        lineHeight = 19.sp,
        modifier = Modifier.absolutePadding(0.dp, 0.dp, 0.dp, 8.dp),
        color = Color(0xFF989898)
    )
}

@Composable
fun DuaraMemberItem(duaraRemoteLocal: DuaraRemote, navController: NavController) {
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier.clickable {
            scope.launch { startMaongeziNaMtu(duaraRemoteLocal, navController) }
        }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.absolutePadding(11.dp, 0.dp, 11.dp, 8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_member_duara),
                contentDescription = "profile_pic",
                modifier = Modifier.size(80.dp)
            )
            Text(
                text = duaraRemoteLocal.nickname[0].toString(),
                fontWeight = FontWeight(500),
                color = Color.White,
                fontSize = 24.sp
            )
        }
        Text(
            text = duaraRemoteLocal.nickname,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF8E8E8E),
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private suspend fun startMaongeziNaMtu(duaraRemote: DuaraRemote, navController: NavController) {
    val ongezi = Ongezi()
    ongezi.id = duaraRemote.pub!!.x
    ongezi.duara_nickname = duaraRemote.nickname
    ongezi.duara_pub = duaraRemote.pub
    ongezi.duara_id = duaraRemote.id
    saveOngezi(ongezi)
    navController.navigate("ongezi/${ongezi.id}") {
        popUpTo("maongezi")
        launchSingleTop = true
    }
}

//@Composable
//private fun SearchWaliomoKwenyeDuara() {
//    var searchKeyword by remember { mutableStateOf("") }
//    Box(
//        modifier = Modifier
//            .absolutePadding(0.dp, 0.dp, 0.dp, 4.dp)
//            .background(shape = RoundedCornerShape(4.dp), color = Color(0xE9ECECEC))
//            .fillMaxWidth()
//            .height(38.dp),
//        contentAlignment = Alignment.Center
//    ) {
//        BasicTextField(
//            value = searchKeyword,
//            onValueChange = {
//                searchKeyword = it
////                maduaraState.searchDuara(searchKeyword, context)
//            },
//            modifier = Modifier
//                .fillMaxWidth()
//                .absolutePadding(8.dp),
//            maxLines = 1,
//            singleLine = true,
//        )
//    }
//}
