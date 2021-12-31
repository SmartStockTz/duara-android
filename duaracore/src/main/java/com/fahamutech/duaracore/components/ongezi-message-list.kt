package com.fahamutech.duaracore.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.fahamutech.duaracore.models.Message
import com.fahamutech.duaracore.models.UserModel
import com.skydoves.landscapist.coil.CoilImage
import java.util.regex.Pattern
import com.fahamutech.duaracore.R
import com.fahamutech.duaracore.models.MessageType
import com.skydoves.landscapist.CircularReveal
import java.io.File


@Composable
fun OngeziMessageList(messages: List<Message>, user: UserModel, modifier: Modifier) {
    val state = rememberLazyListState()
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp, 16.dp),
        state = state,
        reverseLayout = true
    ) {
        messageByTime(messages).forEach { (date, m) ->
            messageInTimeGroupByOwner(m).forEach { (_, msg) ->
                items(msg) { message ->
                    val hO = msg.indexOf(message) != msg.size - 1
                    if (user.pub!!.x != message.sender_pubkey!!.x) {
                        MessageListItemReceive(hO, message)
                    }
                    if (user.pub!!.x == message.sender_pubkey!!.x) {
                        MessageListItemSent(hO, message)
                    }
                }
            }
            item {
                MessageListTimeStamp(date)
            }
        }
    }
}

private fun messageInTimeGroupByOwner(
    messageList: List<Message>
): MutableMap<String, List<Message>> {
    return messageList.groupBy { m ->
        m.sender_nickname
    }.toMutableMap()
}

private fun messageByTime(messages: List<Message>): MutableMap<String, List<Message>> {
    return messages.groupBy {
        val a = it.date.split(":").toMutableList()
        a.removeLastOrNull()
        a.joinToString("").replace("T", " ")
    }.toMutableMap()
}

@Composable
private fun MessageListItemReceive(hideOwner: Boolean, message: Message) {
    Box(
        modifier = Modifier.absolutePadding(0.dp, 0.dp, 0.dp, 4.dp)
    ) {
        if (!hideOwner) {
            ReceiveProfile(message)
        }
        Column(
            modifier = Modifier.absolutePadding(40.dp)
        ) {
            if (!hideOwner) {
                ReceiverName(message)
            }
            if (message.type == MessageType.IMAGE.toString()) {
                ImageMessageView(message)
            } else SelectionContainer {
                LinkifyText(text = message.content)
            }
        }
    }
}

@Composable
private fun ReceiverName(message: Message) {
    Text(
        text = message.sender_nickname,
        fontWeight = FontWeight(300),
        fontSize = 14.sp,
        lineHeight = 16.sp,
        color = Color(0xFF747474),
        modifier = Modifier.absolutePadding(0.dp, 0.dp, 0.dp, 4.dp)
    )
}

@Composable
private fun ReceiveProfile(message: Message) {
    val context = LocalContext.current
    val imageUrl =
        "${context.getString(R.string.base_server_url)}/account/picture/${message.sender_pubkey?.x}/${message.sender_pubkey?.y}"
    CoilImage(
        imageModel = imageUrl,
        contentScale = ContentScale.Crop,
        placeHolder = ImageVector.vectorResource(id = R.drawable.ic_message_sender_bg),
        error = ImageVector.vectorResource(id = R.drawable.ic_message_sender_bg),
        modifier = Modifier
            .size(30.dp)
            .clip(CircleShape)
    )
}

@Composable
private fun MessageListItemSent(hideOwner: Boolean, message: Message) {
    Box(
        modifier = Modifier.absolutePadding(0.dp, 0.dp, 0.dp, 4.dp)
    ) {
        if (!hideOwner) {
            SenderProfile(message)
        }
        Column(
            modifier = Modifier.absolutePadding(40.dp)
        ) {
            if (!hideOwner) {
                SenderName(message)
            }
            if (message.type == MessageType.IMAGE.toString()) {
                ImageMessageView(message)
            } else SelectionContainer {
                LinkifyText(text = message.content)
            }
        }
    }
}

@Composable
private fun SenderName(message: Message) {
    Text(
        text = message.sender_nickname,
        fontWeight = FontWeight(300),
        fontSize = 14.sp,
        lineHeight = 16.sp,
        color = Color(0xFF747474),
        modifier = Modifier.absolutePadding(0.dp, 0.dp, 0.dp, 4.dp)
    )
}

@Composable
private fun SenderProfile(message: Message) {
    val context = LocalContext.current
    val imageUrl =
        "${context.getString(R.string.base_server_url)}/account/picture/${message.sender_pubkey?.x}/${message.sender_pubkey?.y}"
    CoilImage(
        imageModel = imageUrl,
        contentScale = ContentScale.Crop,
        placeHolder = ImageVector.vectorResource(id = R.drawable.ic_list_item_bg),
        error = ImageVector.vectorResource(id = R.drawable.ic_list_item_bg),
        modifier = Modifier
            .size(30.dp)
            .clip(CircleShape)
    )
}

@Composable
private fun ImageMessageView(message: Message) {
    val scrollState = rememberScrollState()
    var imageViewFlag by remember {
        mutableStateOf(false)
    }
    CoilImage(
        imageModel = File(message.content),
        placeHolder = ImageVector.vectorResource(id = R.drawable.ic_image_placeholder),
        error = ImageVector.vectorResource(id = R.drawable.ic_image_placeholder_error),
        modifier = Modifier
            .widthIn(0.dp, 500.dp)
            .fillMaxWidth()
            .heightIn(78.dp, 200.dp)
            .clip(CircleShape.copy(CornerSize(8.dp)))
            .clickable {
                imageViewFlag = true
            }
    )
    if (imageViewFlag) {
        val configuration = LocalConfiguration.current
        Dialog(
            onDismissRequest = {
                imageViewFlag = false
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .heightIn(configuration.screenHeightDp.dp)
                    .widthIn(configuration.screenWidthDp.dp)
                    .clickable {
                        imageViewFlag = false
                    },
                verticalArrangement = Arrangement.Center
            ) {
//                var scale by remember { mutableStateOf(1f) }
//                val maxScale by remember { mutableStateOf(4f) }
//                val minScale by remember { mutableStateOf(0.7f) }
//                var translation by remember { mutableStateOf(Offset(0f, 0f)) }
//                fun calculateNewScale(k: Float): Float {
//                    return if ((scale <= maxScale && k > 1f) || (scale >= minScale && k < 1f)) scale * k else scale
//                }
                CoilImage(
                    imageModel = File(message.content),
                    placeHolder = ImageVector.vectorResource(id = R.drawable.ic_image_placeholder),
                    error = ImageVector.vectorResource(id = R.drawable.ic_image_placeholder_error),
                    modifier = Modifier
//                        .zoomable(onZoomDelta = {
//                            scale = calculateNewScale(it)
//                        })
//                        .rawDragGestureFilter(
//                            object : DragObserver {
//                                override fun onDrag(dragDistance: Offset): Offset {
//                                    translation = translation.plus(dragDistance)
//                                    return super.onDrag(dragDistance)
//                                }
//                            })
//                        .graphicsLayer(
//                            scaleX = scale,
//                            scaleY = scale,
//                            translationX = translation.x,
//                            translationY = translation.y
//                        )
                        .widthIn(0.dp, 500.dp)
                        .fillMaxWidth()
//                        .fillMaxHeight()
                        .clip(CircleShape.copy(CornerSize(8.dp)))
                        .clickable {},
                    circularReveal = CircularReveal(250),
                    contentScale = ContentScale.FillWidth
                )
            }
        }
    }
}

@Composable
private fun MessageListTimeStamp(date: String) {
    Text(
        text = date,
        fontSize = 14.sp,
        color = Color(0xFF3D3D3D),
        fontWeight = FontWeight(200),
        lineHeight = 16.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .absolutePadding(0.dp, 8.dp, 0.dp, 16.dp),
    )
}

@Composable
private fun LinkifyText(text: String, modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current
    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
    val linksList = extractUrls(text)
    val annotatedString = buildAnnotatedString {
        append(text)
        linksList.forEach {
            addStyle(
                style = SpanStyle(
                    color = Color.Blue,
                    textDecoration = TextDecoration.Underline
                ),
                start = it.start,
                end = it.end
            )
            addStringAnnotation(
                tag = "URL",
                annotation = it.url,
                start = it.start,
                end = it.end
            )
        }
    }
    Text(text = annotatedString,
        style = MaterialTheme.typography.body1,
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures { offsetPosition ->
                layoutResult.value?.let {
                    val position = it.getOffsetForPosition(offsetPosition)
                    annotatedString.getStringAnnotations(position, position).firstOrNull()
                        ?.let { result ->
                            if (result.tag == "URL") {
                                uriHandler.openUri(result.item)
                            }
                        }
                }
            }
        },
        onTextLayout = { layoutResult.value = it }
    )
}

private val urlPattern: Pattern = Pattern.compile(
    "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
            + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
            + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
    Pattern.CASE_INSENSITIVE or Pattern.MULTILINE or Pattern.DOTALL
)

private fun extractUrls(text: String): List<LinkInfos> {
    val matcher = urlPattern.matcher(text)
    var matchStart: Int
    var matchEnd: Int
    val links = arrayListOf<LinkInfos>()

    while (matcher.find()) {
        matchStart = matcher.start(1)
        matchEnd = matcher.end()

        var url = text.substring(matchStart, matchEnd)
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "https://$url"

        links.add(LinkInfos(url, matchStart, matchEnd))
    }
    return links
}

private data class LinkInfos(
    val url: String,
    val start: Int,
    val end: Int
)







