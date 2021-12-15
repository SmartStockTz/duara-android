package com.fahamutech.duara.services

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.fahamutech.duara.models.Message
import com.fahamutech.duara.models.MessageCID
import com.fahamutech.duara.models.MessageOutBox

@Dao
interface MessageStorage {
    @Insert
    suspend fun save(message: Message)

    @Query("select * from message where maongezi_id is (:ongeziId) order by date DESC")
    suspend fun maongeziMessages(ongeziId: String): MutableList<Message>

    @Query("select * from message where maongezi_id is (:ongeziId) order by date DESC limit 1")
    suspend fun maongeziLastMessage(ongeziId: String): Message?

    @Query("delete from message where maongezi_id is (:ongeziId)")
    suspend fun deleteMaongeziMessages(ongeziId: String)
}

@Dao
interface MessageOutBoxStorage {
    @Insert
    suspend fun save(messageOutBox: MessageOutBox)

    @Query("select * from message_outbox")
    suspend fun all(): List<MessageOutBox>

    @Query("delete from message_outbox where id is (:id)")
    suspend fun deleteById(id: String)
}

@Dao
interface MessageCidStorage {
    @Insert
    suspend fun save(messageCid: MessageCID)

    @Query("select * from message_cid")
    suspend fun all(): List<MessageCID>

    @Query("delete from message_cid where cid is (:cid)")
    suspend fun delete(cid: String)
}







