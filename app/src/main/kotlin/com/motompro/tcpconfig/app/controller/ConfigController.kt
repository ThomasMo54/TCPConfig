package com.motompro.tcpconfig.app.controller

import com.motompro.tcpconfig.app.config.Config
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.input.MouseEvent
import javafx.scene.text.Text

class ConfigController {

    var config: Config? = null
        set(value) {
            field = value
            if (value == null) return
            configNameText.text = value.name
            configAdapterText.text = value.networkAdapter
            val properties = mutableListOf(value.ip, value.subnetMask)
            if (value.defaultGateway != null) properties.add(value.defaultGateway!!)
            configPropertiesText.text = properties.joinToString("  |  ")
        }
    private lateinit var buttons: Set<Button>

    @FXML
    private lateinit var configNameText: Text
    @FXML
    private lateinit var configAdapterText: Text
    @FXML
    private lateinit var configPropertiesText: Text
    @FXML
    private lateinit var useButton: Button
    @FXML
    private lateinit var editButton: Button
    @FXML
    private lateinit var exportButton: Button
    @FXML
    private lateinit var removeButton: Button

    @FXML
    private fun initialize() {
        buttons = setOf(useButton, editButton, exportButton, removeButton)
    }

    @FXML
    private fun onMouseEnterPane(event: MouseEvent) {
        buttons.forEach { it.isVisible = true }
    }

    @FXML
    private fun onMouseExitPane(event: MouseEvent) {
        buttons.forEach { it.isVisible = false }
    }

    @FXML
    private fun onUseButtonClick(event: ActionEvent) {

    }

    @FXML
    private fun onEditButtonClick(event: ActionEvent) {

    }

    @FXML
    private fun onExportButtonClick(event: ActionEvent) {

    }

    @FXML
    private fun onRemoveButtonClick(event: ActionEvent) {

    }
}
