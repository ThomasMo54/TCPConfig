package com.motompro.tcpconfig.app.netinterfacemanager

import com.motompro.tcpconfig.app.config.Config

interface NetInterfaceManager {

    /**
     * *true* if the computer is connected to a proxy server, *false* otherwise
     */
    var isProxyActivated: Boolean

    /**
     * Get the connected network interfaces names
     */
    val netInterfaces: List<String>

    /**
     * Get the current config of the given network interface
     * @param netInterface the network interface's description
     * @return the interface's config
     */
    fun getConfig(netInterface: String): Config

    /**
     * Get the current usage of DHCP of the given network interface
     * @param netInterface the network interface's description
     * @return *true* if it has a static IP, *false* otherwise
     */
    fun interfaceHasStaticIP(netInterface: String): Boolean

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
