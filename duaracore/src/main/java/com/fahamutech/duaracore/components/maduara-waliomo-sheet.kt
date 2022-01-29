package com.fahamutech.duaracore.components

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fahamutech.duaracore.models.DuaraRemote
import com.fahamutech.duaracore.models.Maongezi
import com.fahamutech.duaracore.services.DuaraStorage
import com.skydoves.landscapist.coil.CoilImage
import kotlinx.coroutines.launch
import com.fahamutech.duaracore.R

@Composable
fun DuaraMemberItem(duaraRemoteLocal: DuaraRemote, navController: NavController, context: Context) {
    val scope = rememberCoroutineScope()
    Row(
        modifier = Modifier.clickable {
            scope.launch { startMaongeziNaMtu(duaraRemoteLocal, navController, context) }
        }.absolutePadding(0.dp,8.dp,0.dp,8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.absolutePadding(11.dp, 4.dp, 11.dp, 8.dp)
        ) {
            val imageUrl = duaraRemoteLocal.picture
            CoilImage(
                imageModel = imageUrl,
                contentDescription = "profile_pic",
                contentScale = ContentScale.Crop,
                placeHolder = ImageVector.vectorResource(id = R.drawable.ic_member_duara),
                error = ImageVector.vectorResource(id = R.drawable.ic_member_duara),
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape.copy(CornerSize(15.dp))),
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth()
                .absolutePadding(4.dp,8.dp,4.dp,8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = duaraRemoteLocal.nickname,
                fontWeight = FontWeight.Normal,
//                color = Color(0xFF8E8E8E),
                fontSize = 16.sp,
//                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
                    .absolutePadding(0.dp,0.dp,16.dp,2.dp)
            )
            Text(
                text = duaraRemoteLocal.description,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF8E8E8E),
                fontSize = 14.sp,
//                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
                    .absolutePadding(0.dp,0.dp,16.dp,8.dp)
            )
        }
    }
}

private suspend fun startMaongeziNaMtu(
    duaraRemote: DuaraRemote, navController: NavController,
    context: Context
) {
    val ongezi = Maongezi(
        receiver_nickname = duaraRemote.nickname,
        receiver_pubkey = duaraRemote.pub,
        receiver_duara_id = duaraRemote.id,
        id = duaraRemote.pub!!.x,
    )
    val storage = DuaraStorage.getInstance(context)
    storage.maongezi().saveOngezi(ongezi)
    navController.navigate("maongezi/${ongezi.id}") {
        popUpTo("maongezi")
        launchSingleTop = true
    }
}

