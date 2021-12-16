package com.fahamutech.duara.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fahamutech.duara.utils.stringFromDate
import java.util.*

@Entity(
    tableName = "maongezi"
)
data class Maongezi(
    @PrimaryKey
    var id: String = UUID.randomUUID().toString(),
    var date: String = stringFromDate(Date()),
    var receiver_nickname: String = "",
    @Embedded(prefix = "receiver_pubkey_")
    var receiver_pubkey: PubModel? = null,
    var receiver_duara_id: String? = null
)