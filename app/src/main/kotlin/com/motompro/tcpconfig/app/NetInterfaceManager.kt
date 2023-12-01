package com.motompro.tcpconfig.app

import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader

private const val NET_INTERFACE_MANAGER_SCRIPT = "NetInterfaceManager.exe"

class NetInterfaceManager {

    private val appPath = File(TCPConfigApp::class.java.protectionDomain.codeSource.location.path).parentFile.absolutePath

    val netInterfaces: List<String>
        get() {
            return try {
                val reader = startManagerProcess(listOf("$appPath\\$NET_INTERFACE_MANAGER_SCRIPT", "getinterfaces"))
                val interfaceAmount = reader.readLine()?.toInt() ?: 0
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
        val command = mutableListOf("cmd", "/c", "")
        command.addAll(args)
        val builder = ProcessBuilder(args)
        return BufferedReader(InputStreamReader(builder.start().inputStream))
    }
}
