package com.fahamutech.duaracore.workes;

import com.fahamutech.duaracore.models.Subscription
import com.fahamutech.duaracore.utils.stringFromDate
import com.fahamutech.duaracore.workers.isNotPaidOrExpire
import org.junit.Assert
import org.junit.Test
import java.util.*

class BillingUnitTests {
    @Test
    fun isNotPaidOrExpire_false_if_paid_and_not_expired() {
        val subscription = Subscription(
            paid = true,
            expire = stringFromDate(Date(Date().time + 100000)),
            reference = "abc"
        )
        val b = isNotPaidOrExpire(subscription)
        Assert.assertEquals(b, false)
    }
    @Test
    fun isNotPaidOrExpire_true_if_paid_and_date_expire() {
        val subscription = Subscription(
            paid = true,
            expire = stringFromDate(Date(Date().time - 100000)),
            reference = "abc"
        )
        val b = isNotPaidOrExpire(subscription)
        Assert.assertEquals(b, true)
    }
    @Test
    fun isNotPaidOrExpire_false_if_paid_false() {
        val subscription = Subscription(
            paid = false,
            expire = stringFromDate(Date(Date().time + 100000)),
            reference = "abc"
        )
        val b = isNotPaidOrExpire(subscription)
        Assert.assertEquals(b, false)
    }
}
