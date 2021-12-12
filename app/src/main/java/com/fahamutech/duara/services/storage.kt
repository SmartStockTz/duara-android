package com.fahamutech.duara.services

import android.content.Context
import com.fahamutech.duara.models.Duara
import com.fahamutech.duara.models.Ongezi
import com.fahamutech.duara.models.UserModel
import io.realm.*
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
//    return withContext(Dispatchers.IO){
    val u = getRealm().where(UserModel::class.java)
        .equalTo("id", "duara_user").findFirst()
    return if (u != null) {
        getRealm().copyFromRealm(u)
    } else {
        u
    }
//    }
}

suspend fun saveMaduara(maduara: List<Duara>) {
    withContext(Dispatchers.IO) {
        getRealm().executeTransaction {
            it.delete(Duara::class.java)
            it.insertOrUpdate(maduara)
        }
    }
}

//suspend fun getMaduaraYote(): List<Duara> {
//    return withContext(Dispatchers.IO) {
//        return@withContext getRealm().where(Duara::class.java).findAll().toList()
//    }
//}

suspend fun getMaduaraByDuara(duara: String): List<Duara> {
    return withContext(Dispatchers.IO) {
        val u = getUser()
        val a = getRealm().where(Duara::class.java)
            .equalTo("duara", duara)
            .notEqualTo("pub.x", u?.pub?.x)
            .findAll().toList()
        return@withContext getRealm().copyFromRealm(a)
    }
}

suspend fun countWaliomoKwenyeDuara(duara: String): Int {
    return withContext(Dispatchers.IO) {
        val u = getUser()
        return@withContext getRealm().where(Duara::class.java)
            .equalTo("duara", duara)
            .notEqualTo("pub.x", u?.pub?.x)
            .findAll().count()
    }
}

suspend fun countMaduaraYote(): Long {
    return withContext(Dispatchers.IO) {
        return@withContext getRealm().where(Duara::class.java).count()
    }
}

suspend fun getMaongezi(): List<Ongezi> {
    return withContext(Dispatchers.IO) {
        val o = getRealm().where(Ongezi::class.java)
            .sort("date", Sort.DESCENDING)
            .findAll().toList()
        return@withContext getRealm().copyFromRealm(o)
    }
}

suspend fun getOngeziInStore(id: String): Ongezi? {
    return withContext(Dispatchers.IO) {
        val r = getRealm()
        val a = r.where(Ongezi::class.java).equalTo("id", id).findFirst()
        if (a != null) {
            return@withContext r.copyFromRealm(a)
        } else {
            return@withContext null
        }
    }
}

suspend fun saveOngezi(ongezi: Ongezi) {
    withContext(Dispatchers.IO) {
        getRealm().executeTransaction {
            it.insertOrUpdate(ongezi)
        }
    }
}

suspend fun futaOngeziInStore(ongezi: Ongezi) {
    withContext(Dispatchers.IO) {
        getRealm().executeTransaction {
            it.where(Ongezi::class.java)
                .equalTo("id", ongezi.id)
                .findFirst()?.deleteFromRealm()
        }
    }
}













