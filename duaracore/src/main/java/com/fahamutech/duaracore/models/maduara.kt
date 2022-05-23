package com.fahamutech.duaracore.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

data class MetaDataModel(
    var age: String = "",
    var gender: String = ""
)

data class DuaraSync(
    var nickname: String = "",
    var pub: PubModel = PubModel(),
    var picture: String = "",
    var token: String = "",
    var device: String = "",
    var maduara: List<String> = mutableListOf(),
    var category: String = "",
    var meta: MetaDataModel = MetaDataModel()
)

@Entity(tableName = "maduara")
open class DuaraRemote(
    @PrimaryKey()
    var id: String = UUID.randomUUID().toString(),
    var duara: String = "",
    var nickname: String = "",
    @Embedded(prefix = "pub_")
    var pub: PubModel? = PubModel(),
    var picture: String = "",
    var description: String = "",
    var category: String = ""
)

//class DuaraLocal(
//    var name: String = "",
//    var normalizedNumber: String = ""
//)












