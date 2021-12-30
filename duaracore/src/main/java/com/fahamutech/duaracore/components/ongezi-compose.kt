package com.fahamutech.duaracore.components

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.fahamutech.duaracore.R
import com.fahamutech.duaracore.models.Maongezi
import com.fahamutech.duaracore.models.UserModel
import com.fahamutech.duaracore.states.OngeziState
import com.fahamutech.duaracore.utils.getFileChecksum
import com.fahamutech.duaracore.utils.messageToApp
import com.fahamutech.duaracore.utils.stringFromDate
import java.io.*
import java.security.MessageDigest
import java.util.*


@Composable
fun OngeziComposeBottomBar(
    maongezi: Maongezi, ongeziState: OngeziState, user: UserModel,
    context: Context,
) {
    var message by remember { mutableStateOf("") }
    Surface(
        elevation = 5.dp
    ) {
        Column(
            modifier = Modifier
                .absolutePadding(16.dp, 8.dp, 16.dp, 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(shape = RoundedCornerShape(4.dp), color = Color(0xFFF8F7F7))
                    .fillMaxWidth()
                    .defaultMinSize(34.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                BasicTextField(
                    value = message,
                    onValueChange = {
                        message = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    maxLines = 4,
                    singleLine = false,
                    textStyle = TextStyle(
                        fontSize = 16.sp
                    )
                )
                if (message.isBlank()) {
                    Text(
                        text = "Andika hapa...",
                        fontWeight = FontWeight(200),
                        color = Color(0xFF7D7D7D),
                        fontSize = 13.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .absolutePadding(8.dp),
                    )
                }
            }
            Row {
                UploadImage { x ->
                    sendImageMessage(
                        maongezi, ongeziState, x, user, context
                    )
                }
                Spacer(Modifier.weight(1.0f))
                IconButton(
                    onClick = {
                        sendMessage(maongezi, ongeziState, message, user, context)
                        message = ""
                    },
                    enabled = message.isNotBlank()
                ) {
                    Icon(Icons.Sharp.Send, contentDescription = "tuma maneno")
                }
            }
        }
    }

}

fun sendMessage(
    maongezi: Maongezi, ongeziState: OngeziState, message: String,
    userModel: UserModel, context: Context
) {
    ongeziState.saveMessage(maongezi, message, userModel, context)
}

fun sendImageMessage(
    maongezi: Maongezi, ongeziState: OngeziState, message: String,
    userModel: UserModel, context: Context
) {
    ongeziState.saveImageMessage(maongezi, message, userModel, context)
}

@Composable
fun UploadImage(
    uploadImage: (path: String) -> Unit
) {
    val context = LocalContext.current
//    var image by remember { mutableStateOf<CropImageView.CropResult?>(null) }
    val cropImage = rememberLauncherForActivityResult(contract = CropImageContract()) { result ->
        if (result.isSuccessful) {
//            image = result
//            result.getBitmap(context)?.let { uploadImage(it) }
            val file = getAppSpecificImageStorageDirFile(
                result.uriContent,
                result.getUriFilePath(context),
                context
            )
            if (file != null) {
                uploadImage(file.absolutePath)
            }
        } else {
            val exception = result.error
            messageToApp(exception?.message ?: "Imeshindwa weka picha, jaribu tena", context)
        }
    }
//    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
//    image?.let {
//        bitmap = image?.getBitmap(context)
//    }
    Row {
        IconButton(
            onClick = { getImageStart(cropImage, gallery = true, camera = false) }
        ) {
            Icon(
                painterResource(id = R.drawable.ic_baseline_image_24),
                contentDescription = "tuma picha"
            )
        }
        IconButton(
            onClick = {
                getImageStart(cropImage, gallery = false, camera = true)
            }
        ) {
            Icon(
                painterResource(id = R.drawable.ic_baseline_photo_camera_24),
                contentDescription = "tuma picha camera"
            )
        }
    }
}

fun getImageStart(
    cropImage: ManagedActivityResultLauncher<CropImageContractOptions, CropImageView.CropResult>,
    gallery: Boolean = true,
    camera: Boolean = true,
) {
    cropImage.launch(
        options {
//            setGuidelines(CropImageView.Guidelines.ON)
            setAutoZoomEnabled(true)
            setInitialCropWindowPaddingRatio(0f)
            setOutputCompressQuality(60)
//            setFixAspectRatio(true)
            setAspectRatio(16, 10)
            setImageSource(gallery, camera)
        }
    )
}

//fun isExternalStorageWritable(): Boolean {
//    return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
//}
//
//fun isExternalStorageReadable(): Boolean {
//    return Environment.getExternalStorageState() in
//            setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)
//}

fun getAppSpecificImageStorageDirFile(uri: Uri?, path: String?, context: Context): File? {
    // Get the pictures directory that's inside the app-specific directory on
    // external storage.
    if (uri == null) {
        return null
    }
    val cR = context.contentResolver
    val type = cR.getType(uri)
    val mime = MimeTypeMap.getSingleton()
    val ext = mime.getExtensionFromMimeType(type)
    val ins = context.contentResolver.openInputStream(uri)
    val imageName = if (ins != null) {
        "/sent/Duara-${getFileChecksum(MessageDigest.getInstance("MD5"), ins) ?: "nil"}.$ext"
    } else "/sent/Duara-${stringFromDate(Date())}.$ext"
    val file = File(
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
        imageName
    )
    if (file.parentFile?.mkdirs() == false) {
        Log.e("LOG_TAG", "Directory not created")
    }
    var bis: BufferedInputStream? = null
    var bos: BufferedOutputStream? = null
    return try {
        bis = BufferedInputStream(FileInputStream(path ?: "/na"))
        bos = BufferedOutputStream(FileOutputStream(file.absoluteFile, false))
        val buf = ByteArray(1024)
        bis.read(buf)
        do {
            bos.write(buf)
        } while (bis.read(buf) != -1)
        file
    } catch (e: IOException) {
        Log.e("******", e.toString())
        messageToApp(e.toString(), context)
        null
    } finally {
        try {
            bis?.close()
            bos?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        try {
//            val d = DocumentFile.fromSingleUri(context,uri)
//            val n = d?.name
//            d?.delete()
            context.contentResolver.delete(uri, null, null)
//            val n = DocumentsContract.deleteDocument(context.contentResolver,uri)
//            Log.e("+++++++?d name", n.toString())
//            uri.normalizeScheme()?.path?.let { Log.e("+++++++?path", it) }
            File(path ?: "/na").delete()
        } catch (e: IOException) {
//            e.printStackTrace()
            Log.e("+++++++?", e.toString())
        }
    }
}












