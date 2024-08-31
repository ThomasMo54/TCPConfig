package com.motompro.tcpconfig.app.controller

import com.motompro.tcpconfig.app.TCPConfigApp
import com.motompro.tcpconfig.app.component.ConfigComponent
import com.motompro.tcpconfig.app.component.draggabletab.DraggableTab
import com.motompro.tcpconfig.app.config.Config
import com.motompro.tcpconfig.app.config.ConfigManager
import com.motompro.tcpconfig.app.exception.ResetConfigException
import javafx.animation.Interpolator
import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Insets
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.SplitPane
import javafx.scene.control.TextField
import javafx.scene.effect.ColorAdjust
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.BorderPane
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.stage.FileChooser
import javafx.util.Duration
import java.awt.Desktop
import java.net.URL

private val EVEN_CONFIG_NODE_COLOR = Background(BackgroundFill(Color.color(0.95, 0.95, 0.95), CornerRadii.EMPTY, Insets.EMPTY))
private val ODD_CONFIG_NODE_COLOR = Background(BackgroundFill(Color.color(0.9, 0.9, 0.9), CornerRadii.EMPTY, Insets.EMPTY))

private const val MOTOMPRO_WEBSITE = "http://motompro.com"

class TCPController {

    private var currentSearch = ""
    lateinit var tab: DraggableTab

    @FXML
    private lateinit var searchTextField: TextField
    @FXML
    private lateinit var configsList: VBox
    @FXML
    private lateinit var addConfigButton: Button
    @FXML
    private lateinit var importConfigButton: Button
    @FXML
    private lateinit var resetButton: Button

    private val buttons by lazy { listOf(addConfigButton, importConfigButton, resetButton) }

    @FXML
    private fun initialize() {
        updateConfigList()
        searchTextField.textProperty().addListener { _, _, newValue ->
            currentSearch = newValue
            updateConfigList()
        }
        buttons.forEach { MainController.setButtonHoverEffect(it) }
    }

    @FXML
    private fun onAddConfigButtonClick() {
        TCPConfigApp.INSTANCE.mainController.createNewConfigTab()
    }

    @FXML
    private fun onImportConfigButtonClick() {
        val fileChooser = FileChooser()
        fileChooser.extensionFilters.add(
            FileChooser.ExtensionFilter(
                "Fichiers YAML (*.${ConfigManager.CONFIG_FILE_EXTENSION}), Fichiers TCPC (*.${ConfigManager.LEGACY_CONFIG_FILE_EXTENSION}), Fichiers TXT (*.${ConfigManager.LEGACY_SAVE_FILE_EXTENSION})",
                "*.${ConfigManager.CONFIG_FILE_EXTENSION}",
                "*.${ConfigManager.LEGACY_CONFIG_FILE_EXTENSION}",
                "*.${ConfigManager.LEGACY_SAVE_FILE_EXTENSION}",
            ),
        )
        val file = fileChooser.showOpenDialog(TCPConfigApp.INSTANCE.stage) ?: return
        try {
            val config = TCPConfigApp.INSTANCE.configManager.loadConfig(file)
            if (file.extension != ConfigManager.LEGACY_CONFIG_FILE_EXTENSION && file.extension != ConfigManager.LEGACY_SAVE_FILE_EXTENSION) {
                TCPConfigApp.INSTANCE.configManager.saveConfig(config)
            }
            updateConfigList()
            TCPConfigApp.INSTANCE.showInfoAlert("Succès", "La config \"${config.name}\" a bien été importée")
        } catch (_: IllegalArgumentException) {
            TCPConfigApp.INSTANCE.showErrorAlert("Erreur", "Fichier mal formatté (${file.name})")
        }
    }

    @FXML
    private fun onResetButtonClick() {
        val app = TCPConfigApp.INSTANCE
        try {
            app.netInterfaceManager.resetConfig()
            app.activeConfig = null
            app.showInfoAlert("Succès", "Votre configuration TCP a bien été réinitialisée")
        } catch (ex: ResetConfigException) {
            app.showErrorAlert("Erreur", "Impossible de réinitialiser la configuration TCP")
        }
    }

    @FXML
    private fun onProxyCheckBoxClicked() {
        TCPConfigApp.INSTANCE.netInterfaceManager.isProxyActivated = true
    }

    @FXML
    private fun onWebsiteHyperlinkClick() {
        Desktop.getDesktop().browse(URL(MOTOMPRO_WEBSITE).toURI())
    }

    fun updateConfigList() {
        configsList.children.clear()
        val configs = TCPConfigApp.INSTANCE.configManager.configs.values.sortedBy { it.name }
        var even = true
        configs.filter { it.name.contains(currentSearch, true) }.forEach {
            val node = createConfigNode(it)
            node.background = if (even) EVEN_CONFIG_NODE_COLOR else ODD_CONFIG_NODE_COLOR
            node.prefWidthProperty().bind(configsList.widthProperty())
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
        val fxmlLoader = FXMLLoader(TCPConfigApp::class.java.getResource("config-component.fxml"))
        val node = fxmlLoader.load<BorderPane>()
        val controller = fxmlLoader.getController<ConfigComponent>()
        controller.config = config
        controller.mainController = this
        return node
    }

    companion object {

    }
}
