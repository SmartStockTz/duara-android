package com.fahamutech.duara.services

import android.content.Context
import com.fahamutech.duara.models.Duara
import com.fahamutech.duara.models.Ongezi
import com.fahamutech.duara.models.UserModel
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults
import io.realm.kotlin.where
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun initLocalDatabase(context: Context) {
    Realm.init(context)
}

private fun getRealm(): Realm {
    val configuration = RealmConfiguration.Builder().name("duara")
        .deleteRealmIfMigrationNeeded()
        .build()
    return Realm.getInstance(configuration)
}

suspend fun saveUser(userModel: UserModel) {
    withContext(Dispatchers.IO) {
        userModel.id = "duara_user"
        getRealm().executeTransactionAsync {
            it.insertOrUpdate(userModel)
        }
    }
}

fun getUser(): UserModel? {
    val u = getRealm().where(UserModel::class.java)
        .equalTo("id", "duara_user").findFirst()
    return if (u != null) {
        getRealm().copyFromRealm(u)
    } else {
        u
    }
}

suspend fun saveMaduara(maduara: List<Duara>) {
    withContext(Dispatchers.IO) {
        getRealm().executeTransactionAsync {
            it.insertOrUpdate(maduara)
        }
    }
}

fun getMaduaraYote(): List<Duara> {
    return getRealm().where(Duara::class.java).findAllAsync().toList()
}

fun getMaduaraByDuara(duara: String): List<Duara> {
    return getRealm().where(Duara::class.java)
        .equalTo("duara", duara).findAllAsync().toList()
}

suspend fun countMaduaraYote(): Long {
    return withContext(Dispatchers.IO) {
        return@withContext getRealm().where(Duara::class.java).count()
    }
}

fun getConversions(): List<Ongezi> {
    return getRealm().where(Ongezi::class.java).findAll().toList()
}
