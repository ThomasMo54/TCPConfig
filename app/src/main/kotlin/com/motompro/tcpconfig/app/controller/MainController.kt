package com.motompro.tcpconfig.app.controller

import com.motompro.tcpconfig.app.TCPConfigApp
import com.motompro.tcpconfig.app.config.Config
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.control.Hyperlink
import javafx.scene.control.TextField
import javafx.scene.input.InputMethodEvent
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.BorderPane
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import java.awt.Desktop
import java.net.URL

private val EVEN_CONFIG_NODE_COLOR = Background(BackgroundFill(Color.color(0.93, 0.93, 0.93), CornerRadii.EMPTY, Insets.EMPTY))
private val ODD_CONFIG_NODE_COLOR = Background(BackgroundFill(Color.color(0.9, 0.9, 0.9), CornerRadii.EMPTY, Insets.EMPTY))
private const val MOTOMPRO_WEBSITE = "http://motompro.com"

class MainController {

    private var currentSearch = ""

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
        updateConfigList()
        searchTextField.textProperty().addListener { observable, oldValue, newValue ->
            currentSearch = newValue
            updateConfigList()
        }
    }

    @FXML
    private fun onAddConfigButtonClick(event: ActionEvent) {

    }

    @FXML
    private fun onWebsiteHyperlinkClick(event: ActionEvent) {
        Desktop.getDesktop().browse(URL(MOTOMPRO_WEBSITE).toURI());
    }

    fun updateConfigList() {
        configsList.children.clear()
        val configs = TCPConfigApp.INSTANCE.configManager.configs.values.sortedBy { it.name }
        var even = true
        configs.filter { it.name.contains(currentSearch, true) }.forEach {
            val node = createConfigNode(it)
            node.background = if (even) EVEN_CONFIG_NODE_COLOR else ODD_CONFIG_NODE_COLOR
            configsList.children.add(node)
            even = !even
        }
    }

    /**
     * Create a config node based on the config data
     * @param config the config data
     * @return the config node
     */
    private fun createConfigNode(config: Config): BorderPane {
        val fxmlLoader = FXMLLoader(TCPConfigApp::class.java.getResource("config-view.fxml"))
        val node = fxmlLoader.load<BorderPane>()
        val controller = fxmlLoader.getController<ConfigController>()
        controller.config = config
        controller.mainController = this
        return node
    }
}
