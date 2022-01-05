package com.fahamutech.duaracore.utils

import org.junit.Assert
import org.junit.Test
import java.util.*

class UtilsUnitTests {
    @Test
    fun dateFromString_is_date() {
        val date = dateFromString("2020-01-01T00:00:00")
        val calendar = Calendar.getInstance()
        calendar.time = date
        Assert.assertEquals(calendar.get(Calendar.YEAR), 2020)
        Assert.assertEquals(calendar.get(Calendar.MONTH+1), 1)
        Assert.assertEquals(calendar.get(Calendar.DATE), 1)
    }
}