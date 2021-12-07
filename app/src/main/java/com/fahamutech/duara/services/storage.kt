package com.fahamutech.duara.services

import android.content.Context
import com.fahamutech.duara.models.UserModel
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.kotlin.where

fun initLocalDatabase(context: Context) {
    Realm.init(context)
}

private fun getRealm(): Realm {
    val configuration = RealmConfiguration.Builder().name("duara").build()
    return Realm.getInstance(configuration)
}

fun saveUser(userModel: UserModel) {
    userModel.id = "duara_user"
    getRealm().executeTransactionAsync {
        it.insertOrUpdate(userModel)
    }
}

fun getUser(onFinish: (user: UserModel) -> Unit){
    getRealm().executeTransactionAsync{
        val u = it.where(UserModel::class.java)
            .equalTo("_id","duara_user").findFirstAsync()
        onFinish(u)
    }
}