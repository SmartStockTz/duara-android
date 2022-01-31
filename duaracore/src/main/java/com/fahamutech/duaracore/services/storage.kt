package com.fahamutech.duaracore.services

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.fahamutech.duaracore.models.*

@Database(
    entities = [
        UserModel::class,
        DuaraRemote::class,
        Maongezi::class,
        Message::class,
        MessageCID::class,
        MessageOutBox::class
    ],
    version = 2
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
            context, DuaraDatabase::class.java, "duara-db"
        ).enableMultiInstanceInvalidation()
            .addMigrations(MIGRATION_1_2)
            .build()
    }
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE user ADD COLUMN payment INTEGER NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE user ADD COLUMN description TEXT NOT NULL DEFAULT '' ")
        database.execSQL("ALTER TABLE user ADD COLUMN maduara TEXT NOT NULL DEFAULT '' ")
        database.execSQL("ALTER TABLE user ADD COLUMN type TEXT NOT NULL DEFAULT 'mteja' ")
        database.execSQL("ALTER TABLE maduara ADD COLUMN description TEXT NOT NULL DEFAULT '' ")
        database.execSQL("ALTER TABLE maduara ADD COLUMN category TEXT NOT NULL DEFAULT '' ")
    }
}









