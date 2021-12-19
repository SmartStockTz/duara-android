package com.fahamutech.duara.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey


class PrivModel(
    var kid: String = "",
    var x: String = "",
    var y: String = "",
    var crv: String = "",
    var d: String = "",
    var kty: String = "",
)

class PubModel(
    var kid: String = "",
    var x: String = "",
    var y: String = "",
    var crv: String = "",
    var kty: String = ""
)

open class IdentityModel {
    var priv = PrivModel()
    var pub = PubModel()
}

@Entity(tableName = "user")
data class UserModel(
    @PrimaryKey
    var id: String = "duara_user",
    var nickname: String = "",
    var token: String = "",
    var picture: String = "",
    @Embedded(prefix = "pub_")
    var pub: PubModel? = PubModel(),
    @Embedded(prefix = "priv_")
    var priv: PrivModel? = PrivModel()
)


class XY(
    var x: String? = null,
    var y: String? = null
)

class UpdatePictureRequest(
    var xy: XY? = null,
    var url: String? = null
)

class UpdateNicknameRequest(
    var xy: XY? = null,
    var name: String? = null
)

class UpdateTokenRequest(
    var xy: XY? = null,
    var token: String? = null
)






