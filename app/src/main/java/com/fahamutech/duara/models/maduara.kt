package com.fahamutech.duara.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class DuaraSync {
    var nickname: String = ""
    var pub: PubModel = PubModel()
    var picture: String = ""
    var token: String = ""
    var device: String = ""
    var maduara: List<String> = mutableListOf()
}

open class DuaraRemote : RealmObject() {
    @PrimaryKey
    var id: String = UUID.randomUUID().toString()  // from hash(device)+hash(hash(nNumber))
    var duara: String = "" // hash(hash(nNumber))
    var nickname: String = ""
    var pub: PubModel? = PubModel() // owner in device
    var picture: String = ""
}

class DuaraLocal {
    var name: String = ""
    var normalizedNumber: String = ""
}

