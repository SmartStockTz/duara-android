package com.fahamutech.duara.states

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fahamutech.duara.models.Ongezi

class MaongeziState : ViewModel() {
    private val _maongezi = MutableLiveData<List<Ongezi>>(mutableListOf())
    val maongezi: LiveData<List<Ongezi>> = _maongezi
    fun fetchMaongezi(){

    }
}