package com.fahamutech.duara.services

import android.content.Context
import com.fahamutech.duara.models.*
import com.fahamutech.duara.utils.encryptMessage
import com.fahamutech.duara.utils.stringFromDate
import io.realm.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

fun initLocalDatabase(context: Context) {
    Realm.init(context)
}

private fun getRealm(): Realm {
    val configuration = RealmConfiguration.Builder().name("duara.realm")
        .deleteRealmIfMigrationNeeded()
        .build()
    return Realm.getInstance(configuration)
}

suspend fun saveUser(userModel: UserModel) {
    withContext(Dispatchers.IO) {
        userModel.id = "duara_user"
        getRealm().executeTransaction {
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

suspend fun saveMaduara(maduara: List<DuaraRemote>) {
    withContext(Dispatchers.IO) {
        getRealm().executeTransaction {
            it.where(DuaraRemote::class.java).findAll().deleteAllFromRealm()
            it.insertOrUpdate(maduara)
        }
    }
}

suspend fun getMaduaraByDuaraNumberHash(duaraHash: String): List<DuaraRemote> {
    return withContext(Dispatchers.IO) {
        val u = getUser()
        val a = getRealm().where(DuaraRemote::class.java)
            .equalTo("duara", duaraHash)
            .notEqualTo("pub.x", u?.pub?.x)
            .findAll().toList()
        return@withContext getRealm().copyFromRealm(a)
    }
}

suspend fun getDuaraByPubX(x: String): DuaraRemote? {
    return withContext(Dispatchers.IO) {
        val a =  getRealm().where(DuaraRemote::class.java)
            .equalTo("pub.x", x)
            .findFirst()
        return@withContext if(a!=null){
            getRealm().copyFromRealm(a)
        }else{
            a
        }
    }
}

suspend fun countWaliomoKwenyeDuara(duara: String): Int {
    return withContext(Dispatchers.IO) {
        val u = getUser()
        return@withContext getRealm().where(DuaraRemote::class.java)
            .equalTo("duara", duara)
            .notEqualTo("pub.x", u?.pub?.x)
            .findAll().count()
    }
}

suspend fun countMaduaraYote(): Long {
    return withContext(Dispatchers.IO) {
        return@withContext getRealm().where(DuaraRemote::class.java).count()
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

suspend fun updateOngeziLastSeen(ongeziId: String) {
    withContext(Dispatchers.IO) {
        getRealm().executeTransaction {
            val o = it.where(Ongezi::class.java)
                .equalTo("id", ongeziId)
                .findFirst()
            if (o != null) {
                o.date = stringFromDate(Date())
                it.insertOrUpdate(o)
            }
        }
    }
}

suspend fun futaOngeziInStore(ongezi: Ongezi) {
    withContext(Dispatchers.IO) {
        getRealm().executeTransaction {
            it.where(Ongezi::class.java)
                .equalTo("id", ongezi.id)
                .findFirst()?.deleteFromRealm()
            it.where(MessageLocal::class.java)
                .equalTo("duara_pub.x", ongezi.duara_pub!!.x)
                .findAll().deleteAllFromRealm()
        }
    }
}

suspend fun saveMessageInStore(messageLocal: MessageLocal) {
    val messageOut = encryptMessage(messageLocal)
    withContext(Dispatchers.IO) {
        getRealm().executeTransaction {
            it.insertOrUpdate(messageLocal)
            it.insertOrUpdate(messageOut)
        }
    }
}

suspend fun getOngeziMessagesInStore(ongeziId: String): MutableList<MessageLocal> {
    return withContext(Dispatchers.IO) {
        val a = getRealm().where(MessageLocal::class.java)
            .equalTo("duara_pub.x", ongeziId)
            .sort("date", Sort.DESCENDING)
            .findAll()
        return@withContext if (a != null) {
            getRealm().copyFromRealm(a).toMutableList()
        } else {
            mutableListOf()
        }
    }
}


suspend fun getLastMessageInOngezi(ongeziId: String): String {
    return withContext(Dispatchers.IO) {
        val a = getRealm().where(MessageLocal::class.java)
            .equalTo("duara_pub.x", ongeziId)
//            .equalTo("status", MessageStatus.INBOX.toString())
            .sort("date", Sort.DESCENDING)
            .findFirst()
        return@withContext if (a != null) {
            a.content ?: ""
        } else {
            ""
        }
    }
}


suspend fun getMessagesWaitToBeSent() {
    withContext(Dispatchers.IO) {
        getRealm().where(MessageLocalOutBox::class.java)
            .findAll()
    }
}

suspend fun deleteMessageWaitToBeSentAfterSent(id: String) {
    withContext(Dispatchers.IO) {
        getRealm().executeTransaction {
            it.where(MessageLocalOutBox::class.java)
                .equalTo("id", id)
                .findFirst()?.deleteFromRealm()
        }
    }
}









