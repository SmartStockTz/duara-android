package com.fahamutech.duara.states

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.fahamutech.duara.models.Maongezi
import com.fahamutech.duara.services.DuaraStorage
import com.fahamutech.duara.utils.messageToApp
import com.fahamutech.duara.utils.withTryCatch
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MaongeziState : ViewModel() {
    private val _maongezi = MutableLiveData<List<Maongezi>>(null)
    val maongezi: LiveData<List<Maongezi>> = _maongezi
    fun fetchMaongezi(context: Context) {
        val storage = DuaraStorage.getInstance(context)
        viewModelScope.launch {
            val m = storage.maongezi().getMaongezi()
            Log.e("JUGGG", m.size.toString())
            _maongezi.value = m
        }
    }

    fun futaOngezi(maongezi: Maongezi, context: Context) {
        viewModelScope.launch {
            val storage = DuaraStorage.getInstance(context)
            withTryCatch(run = {
                storage.withTransaction {
                    storage.maongezi().futaOngeziInStore(maongezi.id)
                    storage.message().deleteMaongeziMessages(maongezi.id)
                }
                _maongezi.value = _maongezi.value?.filter {
                    it.id != maongezi.id
                }
            }) {
                messageToApp(it, context)
            }
        }
    }
}