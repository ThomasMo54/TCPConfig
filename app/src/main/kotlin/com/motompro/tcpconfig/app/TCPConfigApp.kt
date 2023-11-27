package com.motompro.tcpconfig.app

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage

private const val DEFAULT_WIDTH = 720.0
private const val DEFAULT_HEIGHT = 480.0

class TCPConfigApp : Application() {

    override fun start(stage: Stage) {
        val fxmlLoader = FXMLLoader(TCPConfigApp::class.java.getResource("main-view.fxml"))
        val scene = Scene(fxmlLoader.load(), DEFAULT_WIDTH, DEFAULT_HEIGHT)
        stage.title = "TCPConfig $VERSION"
        stage.scene = scene
        stage.show()
    }

    companion object {
        const val VERSION = "2.0"
    }
}

fun main() {
    Application.launch(TCPConfigApp::class.java)
}
