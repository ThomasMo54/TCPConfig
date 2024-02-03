package com.motompro.tcpconfig.app.netinterfacemanager

import com.motompro.tcpconfig.app.TCPConfigApp
import com.motompro.tcpconfig.app.config.Config
import com.motompro.tcpconfig.app.exception.ConfigException
import com.motompro.tcpconfig.app.exception.ResetConfigException
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

private const val NET_INTERFACE_MANAGER_SCRIPT = "NetInterfaceManager.exe"

class WindowsNetInterfaceManager : NetInterfaceManager {

    private val appPath = File(TCPConfigApp::class.java.protectionDomain.codeSource.location.path.replace("%20", " ")).parentFile.absolutePath

    override var isProxyActivated: Boolean
        get() = startManagerProcess(listOf("$appPath\\$NET_INTERFACE_MANAGER_SCRIPT", "isproxyenabled")).readLine() != "0"
        set(value) {
            startManagerProcess(listOf("$appPath\\$NET_INTERFACE_MANAGER_SCRIPT", "setproxystate", if (value) "1" else "0"))
        }

    override val netInterfaces: List<String>
        get() {
            val reader = startManagerProcess(listOf("$appPath\\$NET_INTERFACE_MANAGER_SCRIPT", "getinterfaces"))
            val interfaceAmount = reader.readLine()?.toInt() ?: 0
            val interfaces = mutableListOf<String>()
            for (i in 0 until interfaceAmount) {
                interfaces.add(reader.readLine())
            }
            return interfaces
        }

    override fun getConfig(netInterface: String): Config {
        val reader = startManagerProcess(listOf("$appPath\\$NET_INTERFACE_MANAGER_SCRIPT", "getinterfaceconfig", "\"${netInterface}\""))
        val result = reader.readLine() ?: throw ConfigException(ConfigException.Type.INTERFACE_NOT_VALID)
        if (result.startsWith("error")) throw ConfigException(ConfigException.Type.INTERFACE_NOT_CONNECTED)
        val ipAddress = reader.readLine()
        val subnetMask = reader.readLine()
        val gateway = reader.readLine()
        val favDNS = reader.readLine()
        val auxDNS = reader.readLine()
        return Config(
            "",
            netInterface,
            ipAddress,
            subnetMask,
            if (gateway != "null" && gateway.isNotBlank()) gateway else null,
            if (favDNS != "null" && favDNS.isNotBlank()) favDNS else null,
            if (auxDNS != "null" && auxDNS.isNotBlank()) auxDNS else null,
        )
    }

    override fun interfaceHasStaticIP(netInterface: String): Boolean {
        val reader = startManagerProcess(listOf("$appPath\\$NET_INTERFACE_MANAGER_SCRIPT", "hasStaticIP", "\"${netInterface}\""))
        val result = reader.readLine() ?: throw ConfigException(ConfigException.Type.INTERFACE_NOT_VALID)
        if (result.startsWith("error")) throw ConfigException(ConfigException.Type.INTERFACE_NOT_CONNECTED)
        val isStatic = reader.readLine()
        println(isStatic)
        return isStatic.toBoolean()
    }

    override fun applyConfig(config: Config) {
        val commandParams = mutableListOf("$appPath\\$NET_INTERFACE_MANAGER_SCRIPT", "applyconfig", "\"${config.networkAdapter}\"", config.ip, config.subnetMask)
        config.defaultGateway?.let { commandParams.add(it) }
        if (config.preferredDNS != null && config.auxDNS != null) {
            commandParams.addAll(listOf(config.preferredDNS!!, config.auxDNS!!))
        }
        val reader = startManagerProcess(commandParams)
        val result = reader.readLine()
        if (result.startsWith("error")) {
            val errorType = result.split(" ")[1]
            when (errorType) {
                "notconnected" -> throw ConfigException(ConfigException.Type.INTERFACE_NOT_CONNECTED)
                "notfound" -> throw ConfigException(ConfigException.Type.INTERFACE_NOT_FOUND)
                else -> throw ConfigException(ConfigException.Type.NOT_ENOUGH_ARGS)
            }
        }
    }

    override fun resetConfig() {
        val reader = startManagerProcess(listOf("$appPath\\$NET_INTERFACE_MANAGER_SCRIPT", "resetconfig"))
        val result = reader.readLine()
        if (result.startsWith("error")) {
            throw ResetConfigException()
        }
    }

    private fun startManagerProcess(args: List<String>): BufferedReader {
        val command = mutableListOf("cmd", "/c", "")
        command.addAll(args)
        val builder = ProcessBuilder(args)
        return BufferedReader(InputStreamReader(builder.start().inputStream))
    }
}
