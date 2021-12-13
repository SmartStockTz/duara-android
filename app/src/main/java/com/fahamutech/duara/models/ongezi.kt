package com.fahamutech.duara.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class Ongezi : RealmObject() {
    @PrimaryKey
    var id: String = UUID.randomUUID().toString() // duaraRemote.pub.x
    var date: String = ""
    var duara_nickname: String = ""
    var duara_pub: PubModel? = null
//    var duaraRemote: DuaraRemote? = DuaraRemote()
}