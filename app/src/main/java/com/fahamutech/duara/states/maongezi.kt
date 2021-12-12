package com.fahamutech.duara.states

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fahamutech.duara.models.Ongezi
import com.fahamutech.duara.services.futaOngeziInStore
import com.fahamutech.duara.services.getMaongezi
import com.fahamutech.duara.utils.messageToApp
import com.fahamutech.duara.utils.withTryCatch
import kotlinx.coroutines.launch

class MaongeziState : ViewModel() {
    private val _maongezi = MutableLiveData<List<Ongezi>>(null)
    val maongezi: LiveData<List<Ongezi>> = _maongezi
    fun fetMaongezi() {
        viewModelScope.launch {
            val m = getMaongezi()
            _maongezi.value = m
        }
    }

    fun futaOngezi(ongezi: Ongezi, context: Context) {
        viewModelScope.launch {
            withTryCatch(run = {
                futaOngeziInStore(ongezi)
                _maongezi.value = _maongezi.value?.filter {
                    it.id != ongezi.id
                }
            }) {
                messageToApp(it, context)
            }
        }
    }
}