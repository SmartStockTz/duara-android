package com.fahamutech.duara.states

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.fahamutech.duara.models.Maongezi
import com.fahamutech.duara.models.Message
import com.fahamutech.duara.models.UserModel
import com.fahamutech.duara.services.DuaraStorage
import com.fahamutech.duara.services.RECEIVE_MESSAGE_NOTIFICATION_ID
import com.fahamutech.duara.utils.encryptMessage
import com.fahamutech.duara.utils.stringFromDate
import com.fahamutech.duara.workers.startSendMessageWorker
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*


class OngeziState : ViewModel() {
    private var messageListFlow: Job? = null
    private val _messages =
        MutableLiveData<MutableList<Message>>(mutableListOf())
    val messages: LiveData<MutableList<Message>> = _messages

    fun fetchMessage(ongeziId: String, context: Context) {
        val storage = DuaraStorage.getInstance(context)
        messageListFlow = viewModelScope.launch {
            storage.message().maongeziMessagesLive(ongeziId).collect {
//                Log.e("MESSAGE LISTENER", it.size.toString())
                clearNotification(ongeziId, context)
                _messages.value = it.toMutableList()
            }
        }
    }

    fun dispose(){
        messageListFlow?.cancel()
        _messages.value = mutableListOf()
    }

    private fun clearNotification(ongeziId: String, context: Context) {
        NotificationManagerCompat.from(context).cancel(ongeziId, RECEIVE_MESSAGE_NOTIFICATION_ID)
//        val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//        val r = RingtoneManager.getRingtone(context, notification)
//        r.play()
    }


    fun saveMessage(
        maongezi: Maongezi, message: String, userModel: UserModel, context: Context
    ) {
        val storage = DuaraStorage.getInstance(context)
        viewModelScope.launch {
            val date = stringFromDate(Date())
            val messageLocal = Message(
                date = date,
                content = message,
                duara_id = maongezi.receiver_duara_id,
                receiver_pubkey = maongezi.receiver_pubkey,
                sender_pubkey = userModel.pub,
                sender_nickname = userModel.nickname,
                receiver_nickname = maongezi.receiver_nickname,
                maongezi_id = maongezi.id
            )
            val messageOutbox = encryptMessage(messageLocal, context)
            storage.withTransaction {
                storage.message().save(messageLocal)
                storage.messageOutbox().save(messageOutbox)
                storage.maongezi().updateOngeziLastSeen(maongezi.id, date)
            }
//            _messages.value = (mutableListOf(messageLocal) + _messages.value!!).toMutableList()
            startSendMessageWorker(context)
        }
    }

    fun resetMessages() {
        _messages.value = mutableListOf()
    }
}
