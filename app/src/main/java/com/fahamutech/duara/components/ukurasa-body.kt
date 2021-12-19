package com.fahamutech.duara.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.fahamutech.duara.models.UserModel
import com.fahamutech.duara.utils.messageToApp
import com.fahamutech.duara.workers.startUploadAndUpdateProfilePicture
import com.skydoves.landscapist.glide.GlideImage


@Composable
fun UkurasaBody(user: UserModel) {
    val ss = rememberScrollState()
    Column(
        modifier = Modifier
            .verticalScroll(ss)
            .absolutePadding(16.dp, 0.dp, 16.dp, 100.dp)
    ) {
        UserImage(user)
        Divider(color = Color(0xFFCCCCCC))
        UserName(user)
        Divider(color = Color(0xFFCCCCCC))
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun UserName(user: UserModel) {
    val focusRequester = remember {
        FocusRequester()
    }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var nickname by remember { mutableStateOf(user.nickname) }
    Row {
        var enabled by remember { mutableStateOf(false) }
        Icon(
            Icons.Default.Person, contentDescription = "person",
            modifier = Modifier.absolutePadding(0.dp, 20.dp, 8.dp, 20.dp),
            tint = Color(0xFF6D6D6D)
        )
        Column(
            modifier = Modifier
                .weight(1.0f)
                .absolutePadding(0.dp, 10.dp, 0.dp, 10.dp)
        ) {
            Text(
                text = "Jina lako",
                fontSize = 12.sp,
                fontWeight = FontWeight(300),
                modifier = Modifier.absolutePadding(0.dp, 0.dp, 0.dp, 4.dp)
            )
            BasicTextField(
                value = nickname,
                onValueChange = {
                    nickname = it
                },
                enabled = false,
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .absolutePadding(0.dp, 0.dp, 16.dp, 0.dp),
                maxLines = 1,
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        enabled = false
                        updateName(nickname)
                        keyboardController?.hide()
                        focusManager.clearFocus(true)
                    }
                )
            )
        }
//        IconButton(
//            onClick = {
//                if (enabled) {
//                    updateName(nickname)
//                    focusManager.clearFocus(true)
//                } else focusRequester.requestFocus()
//                enabled = !enabled
//            },
//            modifier = Modifier.absolutePadding(4.dp, 10.dp, 0.dp, 10.dp),
//        ) {
//            if (enabled) Icon(
//                Icons.Default.Done, contentDescription = "done_nickname",
//                tint = Color(0xFF6D6D6D)
//            )
//            else Icon(
//                Icons.Default.Edit, contentDescription = "edit_nick_name",
//                tint = Color(0xFF6D6D6D)
//            )
//        }
    }
}


@Composable
fun UserImage(user: UserModel) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        imageUri = uri
        uploadImage(uri, context)
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            imagePickerLauncher.launch("image/*")
        } else {
            messageToApp("Ruhusu kusoma picha ili iweze kuweka picha yako", context)
        }
    }
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .absolutePadding(0.dp, 8.dp, 0.dp, 24.dp)
    ) {
        Row(
            modifier = Modifier.absolutePadding(0.dp, 8.dp)
        ) {
            Spacer(modifier = Modifier.weight(1.0f))
            IconButton(onClick = {
                when (PackageManager.PERMISSION_GRANTED) {
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) -> {
                        imagePickerLauncher.launch("image/*")
                    }
                    else -> {
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }
            }) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "edit_image",
                    tint = Color(0xFF6D6D6D)
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .absolutePadding(0.dp, 0.dp, 0.dp, 20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape.copy(CornerSize(26.dp)))
                    .background(color = Color(0xFFC4C4C4))
            ) {
                if (bitmap.value != null) {
                    Image(
                        bitmap = bitmap.value!!.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }else{
                    GlideImage(
                        imageModel = user.picture
                    )
                }
            }
        }
    }
    imageUri?.let {
        if (Build.VERSION.SDK_INT < 28) {
            bitmap.value = MediaStore.Images
                .Media.getBitmap(context.contentResolver, it)
        } else {
            val source = ImageDecoder
                .createSource(context.contentResolver, it)
            bitmap.value = ImageDecoder.decodeBitmap(source)
        }
    }
}


private fun uploadImage(uri: Uri?, context: Context) {
    if (uri != null) {
        val cR = context.contentResolver
//        val mime = MimeTypeMap.getSingleton()
        val type = cR.getType(uri)
//        if (type != null) {
//            Log.e("TAGA", type)
//        }
        val path = uri.toString()
//        Log.e("TAG", "start upload task $path")
        startUploadAndUpdateProfilePicture(path, type, context)
    }
}

private fun updateName(name: String) {
//    Log.e("TAG NAME", "start update name,  $name")
}





