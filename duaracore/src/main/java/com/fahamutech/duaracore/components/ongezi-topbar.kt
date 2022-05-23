package com.fahamutech.duaracore.components

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fahamutech.duaracore.models.Maongezi
import com.fahamutech.duaracore.states.OngeziState

@Composable
fun OngeziTopBar(
    maongezi: Maongezi,
    ongeziState: OngeziState,
    context: Context, navController: NavController,
    status: String
) {
    OngeziTopBarNormal(maongezi, navController, status) {
    }
}

@Composable
private fun OngeziTopBarNormal(
    maongezi: Maongezi,
    navController: NavController,
    status: String,
    onSearchClick: () -> Unit
) {
    TopAppBar {
        IconButton(onClick = {
            navController.navigate("maongezi") {
                launchSingleTop = true
                popUpTo(0) {
                    inclusive = true
                }
            }
        }) {
            Icon(Icons.Sharp.ArrowBack, "back")
        }
        Column {
            Text(
                text = maongezi.receiver_nickname,
                fontSize = 18.sp,
                fontWeight = FontWeight(500),
                lineHeight = 36.sp,
                modifier = Modifier.padding(start = 16.dp, end = 24.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = status,
                fontSize = 14.sp,
                fontWeight = FontWeight(300),
                modifier = Modifier.absolutePadding(16.dp)
            )
        }
        Spacer(modifier = Modifier.weight(1.0f))
    }
}
//
//@Composable
//private fun OngeziTopBarSearch(
//    ongeziState: OngeziState, context: Context, onSearchClose: () -> Unit
//) {
//    var searchKeyword by remember { mutableStateOf("") }
//    val focusRequester = FocusRequester()
//    TopAppBar {
//        IconButton(onClick = {
//            onSearchClose()
////            maduaraState.searchDuara("", context)
//        }) {
//            Icon(Icons.Default.Close, "close search")
//        }
//        Box(
//            modifier = Modifier
//                .absolutePadding(4.dp, 4.dp, 8.dp, 4.dp)
//                .background(shape = RoundedCornerShape(4.dp), color = Color(0xE9ECECEC))
//                .fillMaxWidth()
//                .height(38.dp),
//            contentAlignment = Alignment.Center
//
//        ) {
//            BasicTextField(
//                value = searchKeyword,
//                onValueChange = {
//                    searchKeyword = it
////                    maduaraState.searchDuara(searchKeyword, context)
//                },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .absolutePadding(8.dp)
//                    .focusRequester(focusRequester),
//                maxLines = 1,
//                singleLine = true,
//            )
//        }
//    }
//    LaunchedEffect("tafuta") {
//        focusRequester.requestFocus()
//    }
//}