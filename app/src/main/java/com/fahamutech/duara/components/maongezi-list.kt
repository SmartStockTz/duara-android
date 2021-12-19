package com.fahamutech.duara.components

import android.content.Context
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fahamutech.duara.R
import com.fahamutech.duara.models.Maongezi
import com.fahamutech.duara.services.DuaraStorage
import com.fahamutech.duara.states.MaongeziState
import com.fahamutech.duara.ui.theme.DuaraGreen
import com.fahamutech.duara.utils.timeAgo
import kotlinx.coroutines.launch



@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun MaongeziList(
    maongezi: List<Maongezi>, maongeziState: MaongeziState, navController: NavController,
    context: Context
) {
    val st = rememberLazyListState()
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxWidth()
            .absolutePadding(0.dp,0.dp,0.dp,100.dp),
        state = st
    ) {
        items(maongezi) { ongezi ->
            MaongeziItem(ongezi, maongeziState, navController, context)
        }
    }
}






