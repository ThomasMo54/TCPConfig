package com.motompro.tcpconfig.app

import com.motompro.tcpconfig.app.config.Config
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

private const val NET_INTERFACE_MANAGER_SCRIPT = "NetInterfaceManager.exe"

class NetInterfaceManager {

    val netInterfaces: List<String>
        get() {
            return try {
                val reader = startManagerProcess(listOf("getinterfaces"))
                val interfaceAmount = reader.readLine().toInt()
                val interfaces = mutableListOf<String>()
                for (i in 0 until interfaceAmount) {
                    interfaces.add(reader.readLine())
                }
                interfaces
            } catch (ex: IOException) {
                TCPConfigApp.INSTANCE.showErrorAlert("Erreur", ex.stackTraceToString())
                emptyList()
            }
        }

    private fun startManagerProcess(args: List<String>): BufferedReader {
        val command = mutableListOf(NET_INTERFACE_MANAGER_SCRIPT)
        command.addAll(args)
        val builder = ProcessBuilder(args)
        return BufferedReader(InputStreamReader(builder.start().inputStream))
    }
}