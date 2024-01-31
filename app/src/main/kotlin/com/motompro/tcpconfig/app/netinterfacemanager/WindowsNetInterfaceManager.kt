package com.motompro.tcpconfig.app.netinterfacemanager

import com.motompro.tcpconfig.app.TCPConfigApp
import com.motompro.tcpconfig.app.config.Config
import com.motompro.tcpconfig.app.exception.ApplyConfigException
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
                "notconnected" -> throw ApplyConfigException(ApplyConfigException.Type.INTERFACE_NOT_CONNECTED)
                "notfound" -> throw ApplyConfigException(ApplyConfigException.Type.INTERFACE_NOT_FOUND)
                else -> throw ApplyConfigException(ApplyConfigException.Type.NOT_ENOUGH_ARGS)
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
