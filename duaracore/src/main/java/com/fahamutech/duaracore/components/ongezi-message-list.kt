package com.fahamutech.duaracore.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
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
import com.fahamutech.duaracore.models.Message
import com.fahamutech.duaracore.models.UserModel
import com.fahamutech.duaracore.utils.baseUrl
import com.skydoves.landscapist.coil.CoilImage
import java.util.regex.Pattern
import com.fahamutech.duaracore.R


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
            val imageUrl =
                "$baseUrl/account/picture/${message.sender_pubkey?.x}/${message.sender_pubkey?.y}"
            CoilImage(
                imageModel = imageUrl,
                contentScale = ContentScale.Crop,
                placeHolder = ImageVector.vectorResource(id = R.drawable.ic_message_sender_bg),
                error = ImageVector.vectorResource(id = R.drawable.ic_message_sender_bg),
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
//                    .absolutePadding(0.dp, 16.dp, 0.dp, 0.dp),
            )
        }
        Column(
            modifier = Modifier.absolutePadding(40.dp)
        ) {
            if (!hideOwner) {
                Text(
                    text = message.sender_nickname,
                    fontWeight = FontWeight(300),
                    fontSize = 14.sp,
                    lineHeight = 16.sp,
                    color = Color(0xFF747474),
                    modifier = Modifier.absolutePadding(0.dp, 0.dp, 0.dp, 4.dp)
                )
            }
            SelectionContainer {
                LinkifyText(text = message.content)
            }
        }
    }
}

@Composable
private fun MessageListItemSent(hideOwner: Boolean, message: Message) {
    Box(
        modifier = Modifier.absolutePadding(0.dp, 0.dp, 0.dp, 4.dp)
    ) {
        if (!hideOwner) {
            val imageUrl =
                "$baseUrl/account/picture/${message.sender_pubkey?.x}/${message.sender_pubkey?.y}"
            CoilImage(
                imageModel = imageUrl,
                contentScale = ContentScale.Crop,
                placeHolder = ImageVector.vectorResource(id = R.drawable.ic_list_item_bg),
                error = ImageVector.vectorResource(id = R.drawable.ic_list_item_bg),
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
//                    .absolutePadding(0.dp, 16.dp, 0.dp, 0.dp),
            )
        }
        Column(
            modifier = Modifier.absolutePadding(40.dp)
        ) {
            if (!hideOwner) {
                Text(
                    text = message.sender_nickname,
                    fontWeight = FontWeight(300),
                    fontSize = 14.sp,
                    lineHeight = 16.sp,
                    color = Color(0xFF747474),
                    modifier = Modifier.absolutePadding(0.dp, 0.dp, 0.dp, 4.dp)
                )
            }
            SelectionContainer {
                LinkifyText(text = message.content)
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







