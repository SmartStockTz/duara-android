package com.fahamutech.duara.states

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fahamutech.duara.models.DuaraLocal
import com.fahamutech.duara.services.*
import com.fahamutech.duara.utils.message
import com.fahamutech.duara.utils.stringToSHA256
import com.fahamutech.duara.utils.withTryCatch
import kotlinx.coroutines.launch

class MaduaraState : ViewModel() {
    private val _maduaraLocalGroupByInitial =
        MutableLiveData<Map<Char, List<DuaraLocal>>>(mutableMapOf())
    private val _maduaraSyncProgress = MutableLiveData(false)
    private val _waliomoKwenyeDuara = MutableLiveData<MutableMap<String, Int>>(mutableMapOf())

    val maduaraSyncProgress = _maduaraSyncProgress
    val maduaraLocalGroupByInitial: LiveData<Map<Char, List<DuaraLocal>>> =
        _maduaraLocalGroupByInitial
    val waliomoKwenyeDuara: LiveData<MutableMap<String, Int>> = _waliomoKwenyeDuara

    fun fetchMaduara(context: Context) {
        viewModelScope.launch {
            withTryCatch(run = {
                _maduaraSyncProgress.value = true
                val lm = localMaduara(context)
//                lm = lm.map {
//
//                }
                _maduaraLocalGroupByInitial.value = lm.groupBy {
                    it.name[0]
                }
                val yote = countMaduaraYote()
//                Log.e("YOTE", yote.toString())
                if (yote <= 0) {
                    _sm(context)
                } else {
                    _maduaraSyncProgress.value = false
                }
            }) {
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
            }) {
                message(it, context)
            }
        }
    }

    fun woteWaliomoKwenyeDuara(normalizedNumber: String) {
        val a = stringToSHA256(normalizedNumber)
        val b = stringToSHA256(a)
        viewModelScope.launch {
            val c = getMaduaraByDuara(b)
            Log.e("TAG-EFFECT", c.map { it.nickname }.toString())
        }
    }
}











