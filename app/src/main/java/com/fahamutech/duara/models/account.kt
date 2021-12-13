package com.fahamutech.duara.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass(embedded = true)
open class PrivModel : RealmObject() {
    var kid = ""
    var x = ""
    var y = ""
    var crv = ""
    var d = ""
    var kty = ""
}

@RealmClass(embedded = true)
open class PubModel : RealmObject() {
    var kid = ""
    var x = ""
    var y = ""
    var crv = ""
    var kty = ""
}

open class IdentityModel {
    var priv = PrivModel()
    var pub = PubModel()
}

open class UserModel : RealmObject() {
    @PrimaryKey
    var id = "duara_user"
    var nickname = ""
    var token = ""
    var picture = ""
    var pub: PubModel? = PubModel()
    var priv: PrivModel? = PrivModel()
}
