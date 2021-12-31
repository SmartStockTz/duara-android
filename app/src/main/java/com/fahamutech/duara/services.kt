package com.fahamutech.duara

import android.annotation.SuppressLint
import com.fahamutech.duaracore.workers.DuaraFCMService

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class FCMService : DuaraFCMService() {
    override fun getMainActivityClass(): Class<*> {
        return DuaraAppActivity::class.java
    }
}
