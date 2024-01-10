package com.motompro.tcpconfig.app.component.progressdialog

import javafx.fxml.FXML
import javafx.scene.control.ProgressBar
import javafx.scene.control.ProgressIndicator

class ProgressDialogController {

    @FXML
    private lateinit var progressBar: ProgressBar
    @FXML
    private lateinit var progressIndicator: ProgressIndicator

    var progress: Double = -1.0
        set(value) {
            field = value
            progressBar.progress = progress
            progressIndicator.progress = progress
        }

    @FXML
    private fun initialize() {
        progressBar.progress = progress
        progressIndicator.progress = progress
    }
}
