package com.fahamutech.duara.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Ongezi : RealmObject() {
    @PrimaryKey
    var id: String = ""
    var date: String = ""
    var duara: Duara? = Duara()
}