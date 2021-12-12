package com.fahamutech.duara.states

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fahamutech.duara.models.Duara

class OngeziState: ViewModel(){
    private val _duara = MutableLiveData<Duara>(null)
    val duara: LiveData<Duara> = _duara

}