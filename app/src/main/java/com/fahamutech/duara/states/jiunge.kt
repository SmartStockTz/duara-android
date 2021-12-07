package com.fahamutech.duara.states

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fahamutech.duara.models.UserModel
import com.fahamutech.duara.services.ensureContactPermission
import com.fahamutech.duara.services.getIdentity
import com.fahamutech.duara.services.saveUser
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
        fun message() {
            Toast
                .makeText(context, "Jina linatakiwa liwe angalau herudi 3", Toast.LENGTH_SHORT)
                .show()
        }

        val n = nickname.value
        if (n != null) {
            if (n.isEmpty().or(n.length < 3)) {
                message()
            } else {
                ensureContactPermission(context) {
                    _getIdentityProgress.value = true
                    viewModelScope.launch(Dispatchers.Main) {
                        getIdentity(nickname.value!!, context) {
                            it?.nickname?.let { it1 -> Log.e("USER", it1) }
                            _getIdentityProgress.value = false
                            onFinish()
                        }
                    }
                }
            }
        } else {
            message()
        }
    }
}