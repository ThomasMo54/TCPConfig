package com.motompro.tcpconfig.updater

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipInputStream

private val FILES_PATH = "tmp${File.separator}"
private const val FILE_TO_EXECUTE = "TCPConfig.exe"
private const val IS_ZIP = true
private const val ZIP_FILE = "TCPConfig.zip"
private val EXCLUDE_LIST = setOf("jre")

class UpdaterApp

fun main() {
    val currentFileLoc = UpdaterApp::class.java.protectionDomain.codeSource.location.toURI()
    val currentFileName = File(currentFileLoc).name
    val currentDir = File(currentFileLoc).parent

    if (IS_ZIP) {
        val buffer = ByteArray(1024)
        val zipDir = "$currentDir${File.separator}$FILES_PATH$ZIP_FILE"
        val zipInput = ZipInputStream(FileInputStream(zipDir))
        var zipEntry = zipInput.nextEntry
        while (zipEntry != null) {
            if (zipEntry.name == currentFileName || EXCLUDE_LIST.any { zipEntry?.name?.startsWith(it) == true }) {
                zipEntry = zipInput.nextEntry
                continue
            }
            val newFile = File(currentDir, zipEntry.name)
            if (zipEntry.isDirectory) {
                if (!newFile.isDirectory && !newFile.mkdirs()) throw IOException("Failed to create directory $newFile")
            } else {
                val parent = newFile.parentFile
                if (!parent.isDirectory && !parent.mkdirs()) throw IOException("Failed to create directory $parent")
                val output = FileOutputStream(newFile)
                var len: Int
                while (run { len = zipInput.read(buffer); len } > 0) {
                    output.write(buffer, 0, len)
                }
                output.close()
            }
            zipEntry = zipInput.nextEntry
        }
        zipInput.closeEntry()
        zipInput.close()
    }

    File(FILES_PATH).deleteRecursively()
    Runtime.getRuntime().exec("cmd /c start $FILE_TO_EXECUTE")
}
