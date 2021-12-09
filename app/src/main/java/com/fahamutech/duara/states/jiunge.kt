package com.fahamutech.duara.states

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fahamutech.duara.services.ensureContactPermission
import com.fahamutech.duara.services.getIdentity
import com.fahamutech.duara.utils.message
import com.fahamutech.duara.utils.withTryCatch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class JiungeState : ViewModel() {
    private val _getIdentityProgress = MutableLiveData(false)
    val getIdentityProgress: LiveData<Boolean> = _getIdentityProgress

    private val _nickname = MutableLiveData("");
    val nickname: LiveData<String> = _nickname

    fun onNicknameChange(value: String) {
        _nickname.value = value
    }

    fun jiunge(context: Activity, onFinish: () -> Unit) {
        val n = nickname.value
        if (n != null) {
            if (n.isEmpty().or(n.length < 3)) {
                message("Jina linatakiwa liwe angalau herudi 3", context)
            } else {
                ensureContactPermission(context) {
                    _getIdentityProgress.value = true
                    viewModelScope.launch(Dispatchers.Main) {
                        withTryCatch(run = {
                            val identity = getIdentity(nickname.value!!)
                            Log.e("USER", identity.nickname)
                            onFinish()
                            _getIdentityProgress.value = false
                        }){
                            message(it, context)
                        }
                    }
                }
            }
        } else {
            message("Jina linatakiwa liwe angalau herudi 3", context)
        }
    }
}