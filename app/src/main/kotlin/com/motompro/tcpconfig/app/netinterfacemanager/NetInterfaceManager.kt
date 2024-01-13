package com.motompro.tcpconfig.app.netinterfacemanager

import com.motompro.tcpconfig.app.config.Config

interface NetInterfaceManager {

    /**
     * Get the connected network interfaces names
     */
    val netInterfaces: List<String>

    /**
     * Apply the given config's TCP data to the targeted network interface
     * @param config the config
     */
    fun applyConfig(config: Config)

    /**
     * Reset TCP configuration to default values
     */
    fun resetConfig()
}
