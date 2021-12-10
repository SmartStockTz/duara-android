package com.fahamutech.duara.states

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fahamutech.duara.models.UserModel
import com.fahamutech.duara.services.ensureContactPermission
import com.fahamutech.duara.services.getIdentity
import com.fahamutech.duara.services.getUser
import com.fahamutech.duara.utils.message
import com.fahamutech.duara.utils.withTryCatch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class JiungeState : ViewModel() {
    private val _getIdentityProgress = MutableLiveData(false)
    private val _user = MutableLiveData<UserModel?>(null)
    val getIdentityProgress: LiveData<Boolean> = _getIdentityProgress
    val user: LiveData<UserModel?> = _user

    private val _nickname = MutableLiveData("")
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
                            getIdentity(nickname.value!!)
//                            Log.e("USER", identity.nickname)
                            onFinish()
                            loadUser()
                            _getIdentityProgress.value = false
                        }) {
                            message(it, context)
                        }
                    }
                }
            }
        } else {
            message("Jina linatakiwa liwe angalau herudi 3", context)
        }
    }

    fun loadUser() {
        viewModelScope.launch {
            val u = getUser()
            if (u == null) {
                _user.value = null
            } else {
                _user.value = u
            }
        }
    }
}