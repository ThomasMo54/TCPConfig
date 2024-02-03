package com.motompro.tcpconfig.app.dhcp

import com.motompro.tcpconfig.app.dhcp.history.AddressAssignHistory
import com.motompro.tcpconfig.app.dhcp.history.ServerStartHistory

interface DHCPServerListener {

    fun onServerStart(history: ServerStartHistory)

    fun onAddressAssign(history: AddressAssignHistory)
}
