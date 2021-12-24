package com.fahamutech.duara.states

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fahamutech.duara.models.DuaraLocal
import com.fahamutech.duara.models.DuaraRemote
import com.fahamutech.duara.models.UserModel
import com.fahamutech.duara.services.*
import com.fahamutech.duara.utils.messageToApp
import com.fahamutech.duara.utils.withTryCatch
import kotlinx.coroutines.launch

class MaduaraState : ViewModel() {
    private val _maduara = MutableLiveData<List<DuaraRemote>>(mutableListOf())
    private val _maduaraSyncProgress = MutableLiveData(false)

    val maduaraSyncProgress = _maduaraSyncProgress
    val maduara: LiveData<List<DuaraRemote>> = _maduara

    fun fetchMaduara(context: Context) {
        viewModelScope.launch {
            val storage = DuaraStorage.getInstance(context)
            withTryCatch(run = {
                _maduaraSyncProgress.value = true
                val maduara = storage.maduara().getMaduara()
                if (maduara.isEmpty()) {
                    _maduara.value = syncContacts(context).toList()
                } else {
                    _maduara.value = maduara
                    syncMaduara(context)
                }
                _maduaraSyncProgress.value = false
            }) {
                _maduaraSyncProgress.value = false
//                Log.e("Sync results", it)
                messageToApp(it, context)
            }
        }
    }

    fun syncMaduara(context: Context) {
        viewModelScope.launch {
            val storage = DuaraStorage.getInstance(context)
            withTryCatch(run = {
                _maduaraSyncProgress.value = true
                syncContacts(context).toList()
                _maduara.value = storage.maduara().getMaduara()
                _maduaraSyncProgress.value = false
            }) {
//                messageToApp(it, context)
                _maduaraSyncProgress.value = false
            }
        }
    }

    fun searchDuara(keyword: String, context: Context) {
        viewModelScope.launch {
            val storage = DuaraStorage.getInstance(context)
            withTryCatch(run = {
                var lm = storage.maduara().getMaduara()
                lm = lm.filter {
                    it.nickname.lowercase().contains(keyword.lowercase())
                }.toMutableList()
                _maduara.value = lm
            }) {
                messageToApp(it, context)
            }
        }
    }
}











