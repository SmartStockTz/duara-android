package com.fahamutech.duaracore.components

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.fahamutech.duaracore.models.UserModel
import com.fahamutech.duaracore.utils.messageToApp
import com.skydoves.landscapist.coil.CoilImage

@Composable
fun UserImage(
    user: UserModel,
    uploadImage: (uri: Uri?, context: Context) -> Unit
) {
    val context = LocalContext.current
    var image by remember { mutableStateOf<CropImageView.CropResult?>(null) }
    val cropImage = rememberLauncherForActivityResult(contract = CropImageContract()) { result ->
        if (result.isSuccessful) {
            image = result
            uploadImage(image?.uriContent, context)
        } else {
            val exception = result.error
            messageToApp(exception?.message ?: "Imeshindwa weka picha, jaribu tena", context)
        }
    }
    val permissionRequest = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        when {
            it[Manifest.permission.READ_EXTERNAL_STORAGE] == false -> {
                messageToApp("Hujaruhusu kusoma mafile ya picha", context)
            }
            it[Manifest.permission.CAMERA] == false -> {
                messageToApp("Hujaruhusu kuchukua picha kwa camera", context)
            }
            it[Manifest.permission.WRITE_EXTERNAL_STORAGE] == false -> {
                messageToApp("Hujaruhusu kuandika mafile ya picha", context)
            }
            else -> {
                cropImageStart(cropImage)
            }
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
                permissionRequest.launch(permissions())
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
                    .clickable {
                        permissionRequest.launch(permissions())
                    }
            ) {
                if (bitmap.value != null) {
                    Image(
                        bitmap = bitmap.value!!.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    CoilImage(
                        imageModel = user.picture
                    )
                }
            }
        }
    }
    image?.let {
        bitmap.value = image?.getBitmap(context)
    }
}

private fun permissions(): Array<String> {
    return if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.P) {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
    } else arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )
}

private fun cropImageStart(
    cropImage: ManagedActivityResultLauncher<CropImageContractOptions, CropImageView.CropResult>
) {
    cropImage.launch(
        options {
            setGuidelines(CropImageView.Guidelines.ON)
            setAutoZoomEnabled(true)
            setFixAspectRatio(true)
            setAspectRatio(100, 100)
        }
    )
}