package com.fahamutech.duaracore.models

class SubscriptionRequest(
    var x: String? = "",
    var y: String? = "",
    var service: Int? = 1,
    var amount: Int? = 1000,
    var mtoa_huduma_x: String = ""
)

class Subscription(
    var paid: Boolean? = false,
    var expire: String? = "",
    var reference: String? = "",
    var how: String? = ""
)
