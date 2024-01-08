package com.motompro.tcpconfig.app

import com.motompro.tcpconfig.app.config.ConfigManager
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.image.Image
import javafx.stage.Stage
import java.lang.Integer.min

private const val DEFAULT_WIDTH = 720.0
private const val DEFAULT_HEIGHT = 480.0
private const val MAX_ALERT_MESSAGE_LENGTH = 500

class TCPConfigApp : Application() {

    val configManager = ConfigManager()
    val netInterfaceManager = NetInterfaceManager()
    lateinit var stage: Stage
        private set

    override fun start(stage: Stage) {
        INSTANCE = this
        this.stage = stage

        configManager.loadConfigs()

        val fxmlLoader = FXMLLoader(TCPConfigApp::class.java.getResource("main-view.fxml"))
        val scene = Scene(fxmlLoader.load(), DEFAULT_WIDTH, DEFAULT_HEIGHT)
        stage.title = WINDOW_TITLE
        stage.icons.add(Image(TCPConfigApp::class.java.getResourceAsStream("image/app-icon.png")))
        stage.scene = scene
        stage.show()
    }

    fun showErrorAlert(title: String, message: String) {
        val errorAlert = Alert(Alert.AlertType.ERROR)
        errorAlert.title = title
        errorAlert.headerText = title
        errorAlert.contentText = message.substring(0, min(message.length, MAX_ALERT_MESSAGE_LENGTH))
        errorAlert.dialogPane.minWidth = 500.0
        errorAlert.showAndWait()
    }

    fun showInfoAlert(title: String, message: String) {
        val infoAlert = Alert(Alert.AlertType.INFORMATION)
        infoAlert.title = title
        infoAlert.headerText = title
        infoAlert.contentText = message.substring(0, min(message.length, MAX_ALERT_MESSAGE_LENGTH))
        infoAlert.dialogPane.minWidth = 500.0
        infoAlert.showAndWait()
    }

    fun swapScene(sceneView: String) {
        val fxmlLoader = FXMLLoader(TCPConfigApp::class.java.getResource(sceneView))
        val scene = Scene(fxmlLoader.load(), stage.scene.width, stage.scene.height)
        stage.scene = scene
    }

    fun <T> swapScene(sceneView: String): T {
        val fxmlLoader = FXMLLoader(TCPConfigApp::class.java.getResource(sceneView))
        val parent = fxmlLoader.load<Parent>()
        val scene = Scene(parent, stage.scene.width, stage.scene.height)
        stage.scene = scene
        return fxmlLoader.getController()
    }

    companion object {
        const val VERSION = "2.0"
        const val WINDOW_TITLE = "TCPConfig $VERSION"
        lateinit var INSTANCE: TCPConfigApp
            private set
    }
}

fun main() {
    Application.launch(TCPConfigApp::class.java)
}
