package com.fahamutech.duara.models

import com.fahamutech.duara.utils.stringFromDate
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

enum class MessageType {
    TEXT,
    IMAGE
}

enum class MessageStatus {
    READ,
    UNREAD
}

open class MessageRemote {
    // duara_id
    var to: String = ""
    var pub: PubModel? = null
    // encrypted_message(#MessageLocal)
    var message: String = ""
}

open class MessageLocal(
    @PrimaryKey
    var id: String = UUID.randomUUID().toString(),
    var date: String = stringFromDate(Date()),
    var type: String = MessageType.TEXT.toString(),
    var content: String = "",
    var fromNickname: String = "",
    var from: PubModel? = null,
    var duara_id: String? = "", // to
    var duara_pub: PubModel? = null,
    var status: String = MessageStatus.READ.toString()
) : RealmObject()

open class MessageLocalSignature {
    var date: String = stringFromDate(Date())
    @PrimaryKey
    var cid: String? = ""
}

open class MessageLocalOutBox : RealmObject() {
    // remote_duara_hash
    var to: String = UUID.randomUUID().toString() // duara_id
    var from: PubModel? = null
    // encrypted_message(#MessageLocal)
    var message: String = ""
    var date: String = stringFromDate(Date())
}












