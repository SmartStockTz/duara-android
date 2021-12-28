package com.fahamutech.duara

import android.annotation.SuppressLint
import com.fahamutech.duaracore.workers.DuaraFCMService

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class DuaraMessageService : DuaraFCMService() {

    override fun getMainActivityClass(): Class<DuaraAppActivity> {
        return DuaraAppActivity::class.java
    }
}