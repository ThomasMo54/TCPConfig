package com.motompro.tcpconfig.app.controller

import com.motompro.tcpconfig.app.TCPConfigApp
import com.motompro.tcpconfig.app.config.Config
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Node
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
    private fun initialize() {
        val configs = TCPConfigApp.INSTANCE.configManager.configs.values.sortedBy { it.name }
        configs.forEach { configsList.children.add(createConfigNode(it)) }
    }

    @FXML
    private fun onSearchTextChange(event: InputMethodEvent) {

    }

    @FXML
    private fun onAddConfigButtonClick(event: ActionEvent) {

    }

    @FXML
    private fun onWebsiteHyperlinkClick(event: ActionEvent) {

    }

    /**
     * Create a config node based on the config data
     * @param config the config data
     * @return the config node
     */
    private fun createConfigNode(config: Config): Node {
        val fxmlLoader = FXMLLoader()
        val node = fxmlLoader.load<Node>(TCPConfigApp.getResourceStream("config-view.fxml"))
        val controller = fxmlLoader.getController<ConfigController>()
        controller.config = config
        return node
    }
}
