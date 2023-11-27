package com.motompro.tcpconfig.app.controller

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Hyperlink
import javafx.scene.control.TextField
import javafx.scene.input.InputMethodEvent
import javafx.scene.layout.VBox

class MainController {

    @FXML
    private lateinit var searchTextField: TextField
    @FXML
    private lateinit var configsList: VBox
    @FXML
    private lateinit var addConfigButton: Button
    @FXML
    private lateinit var websiteHyperlink: Hyperlink

    @FXML
    private fun onSearchTextChange(event: InputMethodEvent) {

    }

    @FXML
    private fun onAddConfigButtonClick(event: ActionEvent) {

    }

    @FXML
    private fun onWebsiteHyperlinkClick(event: ActionEvent) {

    }
}
