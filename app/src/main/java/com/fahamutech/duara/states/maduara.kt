package com.fahamutech.duara.states

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fahamutech.duara.models.DuaraLocal
import com.fahamutech.duara.services.*
import com.fahamutech.duara.utils.messageToApp
import com.fahamutech.duara.utils.withTryCatch
import kotlinx.coroutines.launch

class MaduaraState : ViewModel() {
    private val _showOneMemberDialog = MutableLiveData(false)
//    private val _showChooseMemberSheet = MutableLiveData(false)
    private val _maduaraLocalGroupByInitial =
        MutableLiveData<Map<Char, List<DuaraLocal>>>(mutableMapOf())
    private val _maduaraSyncProgress = MutableLiveData(false)

    val maduaraSyncProgress = _maduaraSyncProgress
    val maduaraLocalGroupByInitial: LiveData<Map<Char, List<DuaraLocal>>> =
        _maduaraLocalGroupByInitial
    val showOneMemberDialog: LiveData<Boolean> = _showOneMemberDialog
//    val showChooseMemberSheet: LiveData<Boolean> = _showChooseMemberSheet

    fun fetchMaduara(context: Context) {
        viewModelScope.launch {
            withTryCatch(run = {
                _maduaraSyncProgress.value = true
                val lm = localMaduara(context)
                _maduaraLocalGroupByInitial.value = lm.groupBy {
                    it.name[0]
                }
                val yote = countMaduaraYote()
                if (yote <= 0) {
                    _sm(context)
                } else {
                    _maduaraSyncProgress.value = false
                }
            }) {
                _maduaraSyncProgress.value = false
                Log.e("FU Sync results", it)
                messageToApp(it, context)
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
                messageToApp(it, context)
                _maduaraSyncProgress.value = false
            }
        }
    }

//    fun woteWaliomoKwenyeDuara(normalizedNumber: String) {
//        val b = duaraLocalToRemoteHash(normalizedNumber)
//        viewModelScope.launch {
//            val c = getMaduaraByDuara(b)
//            if (c.size == 1) {
//                toggleShowOneMemberDialog(true)
//            } else {
////                toggleShowDuaraMember(true)
//            }
//        }
//    }

//    fun toggleShowDuaraMember(value: Boolean) {
//        _showChooseMemberSheet.value = value
//    }

    fun toggleShowOneMemberDialog(value: Boolean) {
        _showOneMemberDialog.value = value
    }

    fun searchDuara(keyword: String, context: Context) {
        viewModelScope.launch {
            withTryCatch(run = {
                var lm = localMaduara(context)
                lm = lm.filter {
                    it.name.lowercase().contains(keyword.lowercase()).or(
                        it.normalizedNumber.lowercase().contains(keyword.lowercase())
                    )
                }
                _maduaraLocalGroupByInitial.value = lm.groupBy {
                    it.name[0]
                }
            }) {
                messageToApp(it, context)
            }
        }
    }
}











