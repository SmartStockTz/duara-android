package com.fahamutech.duaracore.components

import android.content.Context
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fahamutech.duaracore.components.MaongeziItem
import com.fahamutech.duaracore.models.Maongezi
import com.fahamutech.duaracore.states.MaongeziState


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
        modifier = Modifier.fillMaxWidth().padding(bottom = 54.dp),
        state = st,
    ) {
        items(maongezi) { ongezi ->
            MaongeziItem(ongezi, maongeziState, navController, context)
        }
    }
}






