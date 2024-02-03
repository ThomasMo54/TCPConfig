package com.motompro.tcpconfig.app.dhcp.history

import java.time.ZonedDateTime

abstract class DHCPHistoryItem(
    val date: ZonedDateTime,
) {

    private val timeStamp = "[${date.hour.toString().padStart(2, '0')}:${date.minute.toString().padStart(2, '0')}:${date.second.toString().padStart(2, '0')}]"

    protected abstract val message: String

    fun toMessage(): String {
        return "$timeStamp $message"
    }
}
