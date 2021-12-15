package com.fahamutech.duara.services

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fahamutech.duara.models.UserModel

@Dao
interface UserStorage {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUser(userModel: UserModel)

    @Query("select * from user limit 1")
    suspend fun getUser(): UserModel?
}