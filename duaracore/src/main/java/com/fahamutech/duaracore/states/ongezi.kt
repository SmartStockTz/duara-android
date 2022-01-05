package com.fahamutech.duaracore.states

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.fahamutech.duaracore.models.Maongezi
import com.fahamutech.duaracore.models.Message
import com.fahamutech.duaracore.models.MessageType
import com.fahamutech.duaracore.models.UserModel
import com.fahamutech.duaracore.services.DuaraStorage
import com.fahamutech.duaracore.services.RECEIVE_MESSAGE_NOTIFICATION_ID
import com.fahamutech.duaracore.services.saveMessageLocalForSend
import com.fahamutech.duaracore.utils.stringFromDate
import com.fahamutech.duaracore.workers.startSendImageMessageWorker
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
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
            storage.message().maongeziMessagesLive(ongeziId).distinctUntilChanged().collect {
                clearNotification(ongeziId, context)
                _messages.value = it.toMutableList()
            }
        }
    }

    fun dispose(id: String, context: Context) {
        messageListFlow?.cancel()
        _messages.value = mutableListOf()
        val storage = DuaraStorage.getInstance(context)
        viewModelScope.launch {
            storage.message().markAllRead(id)
        }
    }

    private fun clearNotification(ongeziId: String, context: Context) {
        NotificationManagerCompat.from(context).cancel(ongeziId, RECEIVE_MESSAGE_NOTIFICATION_ID)
    }

    fun saveMessage(
        maongezi: Maongezi, message: String, userModel: UserModel, context: Context
    ) {
        viewModelScope.launch {
            saveMessageLocalForSend(maongezi, message, userModel, context)
        }
    }

    fun saveImageMessage(
        maongezi: Maongezi,
        path: String,
        userModel: UserModel,
        context: Context
    ) {
        val storage = DuaraStorage.getInstance(context)
        viewModelScope.launch {
            val date = stringFromDate(Date())
            val messageLocal = Message(
                date = date,
                content = path,
                duara_id = maongezi.receiver_duara_id,
                receiver_pubkey = maongezi.receiver_pubkey,
                sender_pubkey = userModel.pub,
                sender_nickname = userModel.nickname,
                receiver_nickname = maongezi.receiver_nickname,
                maongezi_id = maongezi.id,
                type = MessageType.IMAGE.toString()
            )
            storage.withTransaction {
                storage.message().save(messageLocal)
                storage.maongezi().updateOngeziLastSeen(maongezi.id, date)
            }
            startSendImageMessageWorker(context, messageLocal)
        }
    }

}
