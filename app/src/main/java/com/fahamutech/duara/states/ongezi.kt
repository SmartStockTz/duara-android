package com.fahamutech.duara.states

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fahamutech.duara.components.Logo
import com.fahamutech.duara.models.DuaraRemote
import com.fahamutech.duara.models.MessageLocal
import com.fahamutech.duara.models.Ongezi
import com.fahamutech.duara.models.UserModel
import com.fahamutech.duara.services.getDuaraByPubX
import com.fahamutech.duara.services.getOngeziMessagesInStore
import com.fahamutech.duara.services.saveMessageInStore
import com.fahamutech.duara.services.updateOngeziLastSeen
import com.fahamutech.duara.utils.messageToApp
import com.fahamutech.duara.utils.stringFromDate
import com.fahamutech.duara.utils.withTryCatch
import kotlinx.coroutines.launch
import java.util.*

class OngeziState : ViewModel() {
    private val _duara = MutableLiveData<DuaraRemote>(null)
    private val _messages =
        MutableLiveData<MutableList<MessageLocal>>(mutableListOf())
    val duaraRemote: LiveData<DuaraRemote> = _duara
    val messages: LiveData<MutableList<MessageLocal>> = _messages

    fun fetchMessage(ongeziId: String) {
        viewModelScope.launch {
            val messages = getOngeziMessagesInStore(ongeziId)
            _messages.value = messages
        }
    }

    fun saveMessage(ongezi: Ongezi, message: String, userModel: UserModel) {
        viewModelScope.launch {
            val duara = getDuaraByPubX(ongezi.duara_pub!!.x)
            val messageLocal = MessageLocal(
                date = stringFromDate(Date()),
                content = message,
                duara_id = duara!!.id,
                duara_pub = duara.pub,
                from = userModel.pub,
                fromNickname = userModel.nickname
            )
            saveMessageInStore(messageLocal)
            updateOngeziLastSeen(ongezi.id)
            val messages = getOngeziMessagesInStore(ongezi.id)
//            Log.e("*******", messages.size.toString())
            _messages.value = messages
        }
    }

    fun resetMessages() {
        _messages.value = mutableListOf()
    }
}
