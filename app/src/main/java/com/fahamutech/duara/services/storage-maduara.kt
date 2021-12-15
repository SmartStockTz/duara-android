package com.fahamutech.duara.services

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fahamutech.duara.models.DuaraRemote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Dao
interface MaduaraStorage {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMaduara(maduara: List<DuaraRemote>)

    @Query("select * from maduara")
    suspend fun getMaduara(): MutableList<DuaraRemote>
}