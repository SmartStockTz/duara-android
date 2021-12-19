package com.fahamutech.duara.services

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fahamutech.duara.models.Message
import com.fahamutech.duara.models.Maongezi
import com.fahamutech.duara.utils.stringFromDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.*

@Dao
interface MaongeziStorage {
    @Query("select * from maongezi order by date DESC")
    fun getMaongezi(): Flow<List<Maongezi>>

    @Query("select * from maongezi where id is (:id)")
    suspend fun getOngeziInStore(id: String): Maongezi?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveOngezi(maongezi: Maongezi)

    @Query("update maongezi set date=(:date) where id is (:ongeziId)")
    suspend fun updateOngeziLastSeen(ongeziId: String, date: String = stringFromDate(Date()))

    @Query("delete from maongezi where id is (:id)")
    suspend fun futaOngeziInStore(id: String)
//    {
//        withContext(Dispatchers.IO) {
//            getRealm().executeTransaction {
//                val o = it.where(Maongezi::class.java)
//                    .equalTo("id", id)
//                    .findFirst()
//                o?.load()
//                o?.deleteFromRealm()
//                val b = it.where(Message::class.java)
//                    .equalTo("maongezi_id", id)
//                    .findAll()
//                b.load()
//                b.deleteAllFromRealm()
//            }
//        }
//    }
}










