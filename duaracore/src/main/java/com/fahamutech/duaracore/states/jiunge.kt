package com.fahamutech.duaracore.states

import android.app.Activity
import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fahamutech.duaracore.models.UserModel
import com.fahamutech.duaracore.services.DuaraStorage
import com.fahamutech.duaracore.services.getIdentity
import com.fahamutech.duaracore.services.mtoaHudumaLogin
import com.fahamutech.duaracore.utils.messageToApp
import com.fahamutech.duaracore.utils.withTryCatch
import com.fahamutech.duaracore.workers.startUploadAndUpdateProfilePicture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class JiungeState : ViewModel() {
    private val _getIdentityProgress = MutableLiveData(false)
    private val _mtoaHudumaProgress = MutableLiveData(false)
    private val _user = MutableLiveData<UserModel?>(null)
    val getIdentityProgress: LiveData<Boolean> = _getIdentityProgress
    val mtoaHudumaProgress: LiveData<Boolean> = _mtoaHudumaProgress
    val user: LiveData<UserModel?> = _user

    private val _nickname = MutableLiveData("")
    val nickname: LiveData<String> = _nickname

    private val _age = MutableLiveData("")
    val age: LiveData<String> = _age

    private val _gender = MutableLiveData("")
    val gender: LiveData<String> = _gender

    private val _password = MutableLiveData("")
    val password: LiveData<String> = _password

    fun onNicknameChange(value: String) {
        _nickname.value = value
    }

    fun onAgeChange(it: String) {
        _age.value = it
    }

    fun onGenderChange(it: String) {
        _gender.value = it
    }

    fun onPasswordChange(it: String) {
        _password.value = it
    }

    fun jiunge(imageUri: Uri?, context: Activity, onFinish: () -> Unit) {
        val n = nickname.value
        val a = age.value
        val g = gender.value
        if (imageUri == null) {
            messageToApp("Bofya ilo boksi apo juu kuweka picha", context);
            return
        }
        if (n == null || n.isEmpty()) {
            messageToApp("Jina linatakiwa liwe angalau herudi 3", context)
            return
        }
        if (a == null || a.isEmpty()) {
            messageToApp("Umri unatakiwa", context)
            return
        }
        if (g == null || g.isEmpty()) {
            messageToApp("Jinsia inatakiwa", context)
            return
        }
        _getIdentityProgress.value = true
        viewModelScope.launch(Dispatchers.Main) {
            withTryCatch(run = {
                val cR = context.contentResolver
                val path = imageUri.toString()
                val u = getIdentity(
                    nickname.value!!.trim(),
                    age.value!!.trim(),
                    gender.value!!.trim(),
                    "", context
                )
                _user.value = u
                val type = cR.getType(imageUri)
                startUploadAndUpdateProfilePicture(path, type, context)
                onFinish()
                _getIdentityProgress.value = false
            }) {
                messageToApp(it, context)
            }
        }
    }

    fun jiungeMtoaHuduma(context: Activity, onFinish: () -> Unit) {
        val n = nickname.value
        val p = password.value
        if (!n.isNullOrEmpty() && !p.isNullOrEmpty()) {
            _mtoaHudumaProgress.value = true
            viewModelScope.launch(Dispatchers.Main) {
                withTryCatch(run = {
                    mtoaHudumaLogin(n, p, context)
                    onFinish()
                    _mtoaHudumaProgress.value = false
                }) {
                    messageToApp(it, context)
                    _mtoaHudumaProgress.value = false
                }
            }
        } else {
            messageToApp("Weka jina na nywila", context)
        }
    }

    fun loadUser(context: Context) {
        viewModelScope.launch {
            val u = DuaraStorage.getInstance(context).user().getUser()
            if (u == null) {
                _user.value = null
            } else {
                _user.value = u
            }
        }
    }
}