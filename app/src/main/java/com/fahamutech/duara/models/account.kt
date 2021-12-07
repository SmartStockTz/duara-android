package com.fahamutech.duara.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class PrivModel : RealmObject() {
    var x = ""
    var crv = ""
    var d = ""
    var kty = ""
}

open class PubModel : RealmObject() {
    var x = ""
    var crv = ""
    var kty = ""
}

class IdentityModel {
    var did = ""
    var priv = PrivModel()
    var pub = PubModel()
}

open class UserModel : RealmObject() {
    @PrimaryKey
    var id = "duara_user"
    var nickname = ""
    var token = ""
    var picture = ""
    var did = ""
    var pub: PubModel? = PubModel()
    var priv: PrivModel? = PrivModel()
}
