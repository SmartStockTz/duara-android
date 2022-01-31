package com.fahamutech.duaracore.models

class PresenceChange(
    var name: String? = "",
    var snapshot: MutableMap<String,Any>? = mutableMapOf()
)

class PresenceBody(
    var info: String? = "",
    var error: Any? = null,
    var change: PresenceChange? = PresenceChange()
)

class PresenceResponse(
    var body: PresenceBody? = null
)
