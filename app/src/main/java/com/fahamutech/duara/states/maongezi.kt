package com.fahamutech.duara.states

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fahamutech.duara.models.Ongezi
import com.fahamutech.duara.services.getMaongezi
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
}