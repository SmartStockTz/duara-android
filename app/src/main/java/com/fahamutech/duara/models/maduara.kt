package com.fahamutech.duara.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class DuaraSync {
    var nickname: String = ""
    var pub: PubModel = PubModel()
    var picture: String = ""
    var token: String = ""
    var maduara: List<String> = mutableListOf()
}

open class Duara : RealmObject() {
    @PrimaryKey
    var id: String = ""
    var duara: String = ""
    var nickname: String = ""
    var pub: PubModel? = PubModel()
    var picture: String = ""
}

class DuaraLocal {
    var name: String = ""
    var normalizedNumber: String = ""
}

