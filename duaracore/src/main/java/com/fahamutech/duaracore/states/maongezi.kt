package com.fahamutech.duaracore.states

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.fahamutech.duaracore.models.Maongezi
import com.fahamutech.duaracore.services.DuaraStorage
import com.fahamutech.duaracore.utils.messageToApp
import com.fahamutech.duaracore.utils.withTryCatch
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MaongeziState : ViewModel() {
    fun futaOngezi(maongezi: Maongezi, context: Context) {
        viewModelScope.launch {
            val storage = DuaraStorage.getInstance(context)
            withTryCatch(run = {
                storage.withTransaction {
                    storage.maongezi().futaOngeziInStore(maongezi.id)
                    storage.message().deleteMaongeziMessages(maongezi.id)
                }
//                _maongezi.value = _maongezi.value?.filter {
//                    it.id != maongezi.id
//                }
            }) {
                messageToApp(it, context)
            }
        }
    }
}