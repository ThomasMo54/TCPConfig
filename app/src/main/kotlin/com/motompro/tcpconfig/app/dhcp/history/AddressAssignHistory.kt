package com.motompro.tcpconfig.app.dhcp.history

import java.time.ZonedDateTime

class AddressAssignHistory(
    date: ZonedDateTime,
    clientName: String,
    ipAddress: String,
) : DHCPHistoryItem(date) {

    override val message = "Le client $clientName a obtenu l'IP $ipAddress"
}
