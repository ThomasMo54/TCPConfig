package com.motompro.tcpconfig.app.dhcp.history

import java.time.ZonedDateTime

class ServerStartHistory(
    date: ZonedDateTime,
    ipAddress: String,
) : DHCPHistoryItem(date) {

    override val message = "Le serveur a démarré sur l'IP $ipAddress"
}
