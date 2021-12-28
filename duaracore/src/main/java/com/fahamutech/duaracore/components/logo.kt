package com.fahamutech.duaracore.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fahamutech.duaracore.R

@Composable
fun Logo(){
    Row(
        modifier = Modifier.absolutePadding(40.dp,100.dp,0.dp,0.dp)
    ) {
        Image(painter = painterResource(id = R.drawable.duaralogo), contentDescription = "logo")
    }
}

@Preview
@Composable
fun PreviewLogo(){
    Logo()
}