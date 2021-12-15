package com.fahamutech.duara.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

data class DuaraSync(
    var nickname: String = "",
    var pub: PubModel = PubModel(),
    var picture: String = "",
    var token: String = "",
    var device: String = "",
    var maduara: List<String> = mutableListOf()
)

@Entity(tableName = "maduara")
open class DuaraRemote(
    @PrimaryKey()
    var id: String = UUID.randomUUID().toString(),
    var duara: String = "",
    var nickname: String = "",
    @Embedded(prefix = "pub_")
    var pub: PubModel? = PubModel(),
    var picture: String = ""
)

class DuaraLocal(
    var name: String = "",
    var normalizedNumber: String = ""
)

