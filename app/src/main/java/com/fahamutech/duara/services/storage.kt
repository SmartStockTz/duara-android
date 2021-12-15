package com.fahamutech.duara.services

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fahamutech.duara.models.*

@Database(
    entities = [
        UserModel::class,
        DuaraRemote::class,
        Maongezi::class,
        Message::class,
        MessageCID::class,
        MessageOutBox::class
    ], version = 1
)
abstract class DuaraDatabase : RoomDatabase() {
    abstract fun maduara(): MaduaraStorage
    abstract fun maongezi(): MaongeziStorage
    abstract fun message(): MessageStorage
    abstract fun messageOutbox(): MessageOutBoxStorage
    abstract fun messageCid(): MessageCidStorage
    abstract fun user(): UserStorage
}

object DuaraStorage {
    fun getInstance(context: Context): DuaraDatabase {
        return Room.databaseBuilder(
            context,
            DuaraDatabase::class.java,
            "duara-db"
        ).enableMultiInstanceInvalidation().build()
    }
}










