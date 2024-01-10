package com.motompro.tcpconfig.app.component.progressdialog

import com.motompro.tcpconfig.app.TCPConfigApp
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle

private const val SCENE_WIDTH = 400.0
private const val SCENE_HEIGHT = 100.0

class ProgressDialog {

    val stage: Stage = Stage()
    private val controller: ProgressDialogController

    var progress: Double = -1.0
        set(value) {
            field = value
            controller.progress = value
        }

    init {
        stage.initStyle(StageStyle.UTILITY)
        stage.isResizable = false
        stage.initModality(Modality.APPLICATION_MODAL)
        val fxmlLoader = FXMLLoader(TCPConfigApp::class.java.getResource("progress-dialog-view.fxml"))
        val parent = fxmlLoader.load<Parent>()
        val scene = Scene(parent, SCENE_WIDTH, SCENE_HEIGHT)
        controller = fxmlLoader.getController()
        stage.scene = scene
    }

    fun show() {
        stage.show()
    }

    fun close() {
        stage.close()
    }
}
