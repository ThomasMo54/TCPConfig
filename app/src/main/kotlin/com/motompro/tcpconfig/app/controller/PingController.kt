package com.motompro.tcpconfig.app.controller

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.layout.VBox

class PingController {

    lateinit var mainController: MainController
    lateinit var pingAddresses: List<String>

    @FXML
    private lateinit var closeButton: Button
    @FXML
    private lateinit var pingResultsVBox: VBox

    @FXML
    private fun initialize() {
        MainController.addDarkenEffect(closeButton)
    }

    @FXML
    private fun onCloseButtonClick() {
        mainController.closePingTab()
    }
}
