package com.motompro.tcpconfig.app

import com.motompro.tcpconfig.app.config.ConfigManager
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.stage.Stage
import java.io.InputStream

private const val DEFAULT_WIDTH = 720.0
private const val DEFAULT_HEIGHT = 480.0

class TCPConfigApp : Application() {

    val configManager = ConfigManager()
    val netInterfaceManager = NetInterfaceManager()

    override fun start(stage: Stage) {
        INSTANCE = this

        configManager.loadConfigs()
        netInterfaceManager.netInterfaces.forEach { println(it) }

        val fxmlLoader = FXMLLoader(TCPConfigApp::class.java.getResource("main-view.fxml"))
        val scene = Scene(fxmlLoader.load(), DEFAULT_WIDTH, DEFAULT_HEIGHT)
        stage.title = "TCPConfig $VERSION"
        stage.scene = scene
        stage.show()
    }

    fun showErrorAlert(title: String, message: String) {
        val errorAlert = Alert(Alert.AlertType.ERROR)
        errorAlert.title = title
        errorAlert.headerText = title
        errorAlert.contentText = message
        errorAlert.showAndWait()
    }

    companion object {
        const val VERSION = "2.0"
        lateinit var INSTANCE: TCPConfigApp
            private set
    }
}

fun main() {
    Application.launch(TCPConfigApp::class.java)
}
