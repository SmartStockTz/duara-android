package com.fahamutech.duara.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fahamutech.duara.utils.stringFromDate
import java.util.*

enum class MessageType {
    TEXT,
    IMAGE,
    ENCRYPTED
}

enum class MessageStatus {
    READ,
    UNREAD
}

class MessageRemote(
    var receiver_pubkey: PubModel?,
    var sender_pubkey: PubModel?,
    var message: String
)

class MessageRemoteResponse {
    var cid: String = ""
    var multicast_id: String? = null
    var success: String? = null
    var failure: String? = null
    var canonical_ids: String? = null
    var results: List<MutableMap<String, Any>> = mutableListOf()
}

@Entity(tableName = "message")
data class Message(
    @PrimaryKey
    var id: String = UUID.randomUUID().toString(),
    var date: String = stringFromDate(Date()),
    var type: String = MessageType.TEXT.toString(),
    var content: String = "",
    var sender_nickname: String = "",
    var receiver_nickname: String = "",
    @Embedded(prefix = "sender_pubkey_")
    var sender_pubkey: PubModel? = null,
    var duara_id: String? = "",
    @Embedded(prefix = "receiver_pubkey_")
    var receiver_pubkey: PubModel? = null,
    var maongezi_id: String? = null,
    var status: String = MessageStatus.READ.toString()
)

@Entity(tableName = "message_cid")
data class MessageCID(
    @PrimaryKey
    var cid: String = UUID.randomUUID().toString(),
    var date: String = stringFromDate(Date())
)

@Entity(tableName = "message_outbox")
data class MessageOutBox(
    @PrimaryKey
    var id: String = UUID.randomUUID().toString(),
    @Embedded(prefix = "sender_pubkey_")
    var sender_pubkey: PubModel? = null,
    @Embedded(prefix = "receiver_pubkey_")
    var receiver_pubkey: PubModel? = null,
    var message: String = "",
    var date: String = stringFromDate(Date())
)












