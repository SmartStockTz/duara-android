package com.fahamutech.duaracore.services

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fahamutech.duaracore.models.Message
import com.fahamutech.duaracore.models.MessageCID
import com.fahamutech.duaracore.models.MessageOutBox
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageStorage {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(message: Message)

    @Query("select * from message where maongezi_id is (:ongeziId) order by date DESC")
    suspend fun maongeziMessages(ongeziId: String): MutableList<Message>

    @Query("select * from message where maongezi_id is (:ongeziId) order by date DESC")
    fun maongeziMessagesLive(ongeziId: String): Flow<List<Message>>

    @Query("select * from message where maongezi_id is (:ongeziId) order by date DESC limit 1")
    fun maongeziLastMessage(ongeziId: String): Flow<Message?>

    @Query("select count(status) from message where status='UNREAD' and maongezi_id=(:ongeziId)")
    fun maongeziUnreadMessage(ongeziId: String): Flow<Int?>

    @Query("select count(status) from message where status='UNREAD'")
    fun totalUnread(): Flow<Int?>

    @Query("select count(distinct maongezi_id) from message where status='UNREAD' group by maongezi_id")
    fun totalUnreadGroup(): Flow<Int?>

    @Query("update message set status='READ' where maongezi_id=(:ongeziId)")
    suspend fun markAllRead(ongeziId: String)

    @Query("delete from message where maongezi_id is (:ongeziId)")
    suspend fun deleteMaongeziMessages(ongeziId: String)
    @Query("delete from message")
    suspend fun deleteAll()
}

@Dao
interface MessageOutBoxStorage {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(messageOutBox: MessageOutBox)

    @Query("select * from message_outbox")
    suspend fun all(): List<MessageOutBox>

    @Query("delete from message_outbox where id is (:id)")
    suspend fun deleteById(id: String)

    @Query("delete from message_outbox")
    suspend fun deleteAll()
}

@Dao
interface MessageCidStorage {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(messageCid: MessageCID)

    @Query("select * from message_cid")
    suspend fun all(): List<MessageCID>

    @Query("delete from message_cid where cid is (:cid)")
    suspend fun delete(cid: String)

    @Query("delete from message_cid")
    suspend fun deleteAll()
}







