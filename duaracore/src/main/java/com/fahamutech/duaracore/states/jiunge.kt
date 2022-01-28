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

    private val _password = MutableLiveData("")
    val password: LiveData<String> = _password

    fun onNicknameChange(value: String) {
        _nickname.value = value
    }

    fun jiunge(imageUri: Uri?, context: Activity, onFinish: () -> Unit) {
        val n = nickname.value
//        if (imageUri == null) {
//            messageToApp("Bofya ilo boksi apo juu kuweka picha", context);
//            return
//        }
        if (n != null) {
            if (n.isEmpty().or(n.length < 3)) {
                messageToApp("Jina linatakiwa liwe angalau herudi 3", context)
            } else {
                _getIdentityProgress.value = true
                viewModelScope.launch(Dispatchers.Main) {
                    withTryCatch(run = {
                        val cR = context.contentResolver
                        val path = imageUri.toString()
                        val u = getIdentity(nickname.value!!, "", context)
                        _user.value = u
                        if (imageUri == null) {
                            onFinish()
                            _getIdentityProgress.value = false
                        } else {
                            val type = cR.getType(imageUri)
                            startUploadAndUpdateProfilePicture(path, type, context)
                            onFinish()
                            _getIdentityProgress.value = false
                        }
                    }) {
                        messageToApp(it, context)
                    }
                }
            }
        } else {
            messageToApp("Jina linatakiwa liwe angalau herudi 3", context)
        }
    }

    fun jiungeMtoaHuduma(context: Activity, onFinish: () -> Unit) {
        val n = nickname.value
        val p = password.value
        if (!n.isNullOrEmpty() && !p.isNullOrEmpty()) {
            _mtoaHudumaProgress.value = true
            viewModelScope.launch(Dispatchers.Main) {
                withTryCatch(run = {
//                    val cR = context.contentResolver
//                    val path = imageUri.toString()
//                    val u = getIdentity(nickname.value!!, "", context)
//                    _user.value = u
//                    if (imageUri == null) {
//                        onFinish()
//                        _getIdentityProgress.value = false
//                    } else {
//                        val type = cR.getType(imageUri)
//                        startUploadAndUpdateProfilePicture(path, type, context)
//                        onFinish()
//                        _getIdentityProgress.value = false
//                    }
                }) {
                    messageToApp(it, context)
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

    fun onPasswordChange(it: String) {
        _password.value = it
    }
}