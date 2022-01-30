package com.fahamutech.duaracore.components

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
    Surface(elevation = 5.dp) {
        Column(modifier = Modifier.absolutePadding(16.dp, 8.dp, 16.dp, 8.dp)) {
            Box(
                modifier = Modifier
                    .background(shape = RoundedCornerShape(4.dp), color = Color(0xFFF8F7F7))
                    .fillMaxWidth()
                    .defaultMinSize(34.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                BasicTextField(
                    value = message,
                    onValueChange = { message = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    maxLines = 4,
                    singleLine = false,
                    textStyle = TextStyle(fontSize = 16.sp)
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
                    sendImageMessage(maongezi, ongeziState, x, user, context)
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
    val cropImage = rememberLauncherForActivityResult(contract = CropImageContract()) { result ->
        if (result.isSuccessful) {
            val file = try {
                getAppSpecificImageStorageDirFile(
                    result.uriContent,
                    result.getUriFilePath(context),
                    context
                )
            } catch (e: Exception) {
                Log.e("APP DIR", e.toString())
                null
            }
            if (file != null) {
                uploadImage(file.absolutePath)
            }
        } else {
            val exception = result.error
            messageToApp(exception?.message ?: "Imeshindwa weka picha, jaribu tena", context)
        }
    }
    Row {
        val readStorageLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            if (it) {
                getImageStart(cropImage, gallery = true, camera = false)
            } else messageToApp("Hujaruhusu kusoma mafile ya picha", context)
        }
        IconButton(
            onClick = { readStorageLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE) }
        ) {
            Icon(
                painterResource(id = R.drawable.ic_baseline_image_24),
                contentDescription = "tuma picha"
            )
        }
        val cameraLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            if (it) {
                getImageStart(cropImage, gallery = false, camera = true)
            } else messageToApp("Hujaruhusu camera kupiga picha", context)
        }
        IconButton(
            onClick = {
                cameraLauncher.launch(Manifest.permission.CAMERA)
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
            setAutoZoomEnabled(true)
            setInitialCropWindowPaddingRatio(0.1f)
            setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
            setOutputCompressQuality(50)
            setImageSource(gallery, camera)
        }
    )
}

fun getAppSpecificImageStorageDirFile(uri: Uri?, path: String?, context: Context): File? {
    if (uri == null) return null
    val scheme = uri.scheme
    val ext = if (scheme == "file") {
        uri.toString().substring(uri.toString().lastIndexOf("."))
    } else {
        val cR = context.contentResolver
        val type = cR.getType(uri)
        val mime = MimeTypeMap.getSingleton()
        mime.getExtensionFromMimeType(type)
    }
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
//            context.contentResolver.delete(uri, null, null)
//            val n = DocumentsContract.deleteDocument(context.contentResolver,uri)
//            Log.e("+++++++?d name", n.toString())
//            uri.normalizeScheme()?.path?.let { Log.e("+++++++?path", it) }
            if (scheme == "content")
                context.contentResolver.delete(uri, null, null)
            File(path ?: "/na").delete()
        } catch (e: IOException) {
            Log.e("+++++++?", e.toString())
        }
    }
}












