package com.motompro.tcpconfig.updater

import java.io.BufferedReader
import java.io.InputStreamReader

class UpdaterApp

fun main() {
    val version = readVersion()

}

private fun readVersion(): String {
    val reader = BufferedReader(InputStreamReader(UpdaterApp::class.java.getResourceAsStream("/version.txt") ?: return "?"))
    val version = reader.readLine().trim()
    reader.close()
    return version
}