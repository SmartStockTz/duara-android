package com.fahamutech.duara.states

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fahamutech.duara.models.DuaraLocal
import com.fahamutech.duara.services.countMaduaraYote
import com.fahamutech.duara.services.localMaduara
import com.fahamutech.duara.services.syncContacts
import com.fahamutech.duara.utils.message
import com.fahamutech.duara.utils.withTryCatch
import kotlinx.coroutines.launch

class MaduaraState : ViewModel() {
    //    private val _localNormalizedContacts = MutableLiveData<List<String>>(mutableListOf())
    private val _maduaraLocal = MutableLiveData<List<DuaraLocal>>(mutableListOf())
    private val _maduaraSyncProgress = MutableLiveData(false)

    //    val localNormalizedNumbers = _localNormalizedContacts
    val maduaraSyncProgress = _maduaraSyncProgress
    val maduaraLocal: LiveData<List<DuaraLocal>> = _maduaraLocal

//    fun fetchLocalNormalisedContacts(context: Context) {
//        viewModelScope.launch {
//            val numbers = normalisedNumberSignatures(context)
//            _localNormalizedContacts.value = numbers
//        }
//    }

    fun fetchMaduara(context: Context) {
        viewModelScope.launch {
            withTryCatch(run = {
                _maduaraSyncProgress.value = true
                val lm = localMaduara(context)
                _maduaraLocal.value = lm
                val yote = countMaduaraYote()
                if (yote <= 0) {
                    _sm(context)
                }else{
                    _maduaraSyncProgress.value = false
                }
            }){
                _maduaraSyncProgress.value = false
                Log.e("FU Sync results", it)
                message(it, context)
            }
        }
    }

    private suspend fun _sm(context: Context) {
        _maduaraSyncProgress.value = true
        syncContacts(context)
        _maduaraSyncProgress.value = false
    }

    fun syncMaduara(context: Context) {
        viewModelScope.launch {
            withTryCatch(run = {
                _sm(context)
            }){
                message(it, context)
            }
        }
    }

}