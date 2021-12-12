package com.fahamutech.duara.components

import android.content.Context
import android.text.format.DateUtils
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fahamutech.duara.R
import com.fahamutech.duara.models.Ongezi
import com.fahamutech.duara.states.MaongeziState
import com.fahamutech.duara.ui.theme.DuaraGreen
import com.fahamutech.duara.utils.dateFromString
import java.util.*

@Composable
fun MaongeziTopBar() {
    TopAppBar {
        Text(
            text = "Maongezi",
            fontSize = 24.sp,
            fontWeight = FontWeight(500),
            lineHeight = 36.sp,
            modifier = Modifier.absolutePadding(16.dp)
        )
    }
}

@Composable
fun MaongeziMapyaFAB(navController: NavController) {
    FloatingActionButton(
        onClick = {
            navController.navigate("maduara") {
                launchSingleTop = true
            }
        },
        backgroundColor = DuaraGreen
    ) {
        Icon(Icons.Sharp.Add, contentDescription = "maongezi mapya")
    }
}

@ExperimentalFoundationApi
@Composable
fun ListYaMaongeziYote(
    maongezi: List<Ongezi>, maongeziState: MaongeziState, navController: NavController,
    context: Context
) {
    val st = rememberLazyListState()
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxWidth(),
        state = st
    ) {
        items(maongezi) { ongezi ->
            OngeziItem(ongezi, maongeziState, navController, context)
        }
    }
}

@ExperimentalFoundationApi
@Composable
private fun OngeziItem(
    ongezi: Ongezi,
    maongeziState: MaongeziState,
    navController: NavController,
    context: Context
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .absolutePadding(0.dp)
            .combinedClickable(
                onClick = {
                    navController.navigate("ongezi/${ongezi.id}") {
                        launchSingleTop = true
                    }
                },
                onLongClick = {
                    showDeleteDialog = true
                }
            ),
    ) {
        OngeziItemPicture(ongezi)
        Column(
            modifier = Modifier.absolutePadding(6.dp, 8.dp, 16.dp, 8.dp),
        ) {
            OngeziItemNameAndTime(ongezi)
            OngeziItemLastMessage(ongezi)
        }
    }
    ShowDeleteConversationDialog(showDeleteDialog) {
        when (it) {
            "n" -> {
                maongeziState.futaOngezi(ongezi, context)
                showDeleteDialog = false
            }
            "h" -> {
                showDeleteDialog = false
            }
            "f" -> {
                showDeleteDialog = false
            }
        }
    }

}

@Composable
private fun ShowDeleteConversationDialog(
    showDeleteDialog: Boolean, onClose: (action: String) -> Unit
) {
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { onClose("f") },
            text = {
                Text("Unataka futa maongezi haya?")
            },
            dismissButton = {
                Button(onClick = { onClose("h") }) {
                    Text(text = "Hapana")
                }
            },
            confirmButton = {
                Button(onClick = { onClose("n") }) {
                    Text(text = "Ndio")
                }
            }
        )
    }
}

@Composable
private fun OngeziItemLastMessage(ongezi: Ongezi) {
    val message by remember { mutableStateOf("") }
    Text(
        text = message,
        fontWeight = FontWeight(300),
        fontSize = 13.sp,
        color = Color(0xFF747272)
    )
}

@Composable
private fun OngeziItemNameAndTime(ongezi: Ongezi) {
    Row {
        Text(
            text = ongezi.duara!!.nickname,
            fontWeight = FontWeight(500),
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.weight(1.0f))
        Text(
            text = timeAgo(ongezi.date),
            color = Color(0xFF747272),
            fontWeight = FontWeight(400),
            fontSize = 14.sp,
//                    lineHeight = 24.sp
        )
    }
}

@Composable
private fun OngeziItemPicture(ongezi: Ongezi) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.absolutePadding(16.dp, 8.dp, 0.dp, 8.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_list_item_bg),
            contentDescription = "profile picture",
            modifier = Modifier.size(44.dp)
        )
//            Icon(
//                Icons.Sharp.Person,
//                contentDescription = "picture",
//                tint = Color.White
//            )
        Text(
            text = ongezi.duara!!.nickname[0].toString(),
            fontWeight = FontWeight(400),
            color = Color.White,
            fontSize = 16.sp
        )
    }
}

private fun timeAgo(dateString: String): String {
    return try {
        val date: Date = dateFromString(dateString)
        DateUtils.getRelativeTimeSpanString(date.time).toString()
            .replace("ago", "")
            .replace("hours", "h")
            .replace("hour", "h")
            .replace("minutes", "min")
            .replace("yesterday", "Jana")
            .replace("Yesterday", "Jana")
    } catch (_: Throwable) {
        ""
    }
}

@Composable
fun HamnaMaongezi() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.size(100.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.duaralogo),
                contentDescription = "hamna"
            )
        }
        Text(
            "Bado hauna maongezi na mtu, " +
                    "bofya apo chini kulia kuanzisha maongezi mapya.",
            modifier = Modifier.absolutePadding(24.dp, 0.dp, 24.dp, 0.dp),
            fontWeight = FontWeight(300),
            color = Color(0xFF989898),
            textAlign = TextAlign.Center
        )
    }
}






