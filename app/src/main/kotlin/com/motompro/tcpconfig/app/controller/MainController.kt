package com.motompro.tcpconfig.app.controller

import com.motompro.tcpconfig.app.TCPConfigApp
import com.motompro.tcpconfig.app.component.ConfigComponent
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
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.stage.FileChooser
import javafx.util.Duration
import java.awt.Desktop
import java.net.URL

private val EVEN_CONFIG_NODE_COLOR = Background(BackgroundFill(Color.color(0.95, 0.95, 0.95), CornerRadii.EMPTY, Insets.EMPTY))
private val ODD_CONFIG_NODE_COLOR = Background(BackgroundFill(Color.color(0.9, 0.9, 0.9), CornerRadii.EMPTY, Insets.EMPTY))

private const val MOTOMPRO_WEBSITE = "http://motompro.com"

private const val PING_TAB_SIZE_RATIO = 0.25

class MainController {

    private val dhcpServerEnabledIcon = Image(TCPConfigApp::class.java.getResourceAsStream("image/dhcp-enabled-icon.png"))

    private var currentSearch = ""
    private var pingController: PingController? = null

    @FXML
    private lateinit var searchTextField: TextField
    @FXML
    private lateinit var configsList: VBox
    @FXML
    private lateinit var splitPane: SplitPane
    @FXML
    private lateinit var dhcpButtonImage: ImageView
    @FXML
    private lateinit var proxyCheckBox: CheckBox

    @FXML
    private fun initialize() {
        updateConfigList()
        searchTextField.textProperty().addListener { _, _, newValue ->
            currentSearch = newValue
            updateConfigList()
        }
        proxyCheckBox.isSelected = TCPConfigApp.INSTANCE.netInterfaceManager.isProxyActivated

        if (TCPConfigApp.INSTANCE.dhcpServer.isStarted) {
            dhcpButtonImage.image = dhcpServerEnabledIcon
        }
    }

    @FXML
    private fun onAddConfigButtonClick() {
        TCPConfigApp.INSTANCE.swapScene("add-edit-config-view.fxml")
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
    private fun onPingButtonClick() {
        TCPConfigApp.INSTANCE.swapScene("create-ping-view.fxml")
    }

    @FXML
    private fun onDhcpButtonClick() {
        TCPConfigApp.INSTANCE.swapScene("dhcp-view.fxml")
    }

    @FXML
    private fun onProxyCheckBoxClicked() {
        TCPConfigApp.INSTANCE.netInterfaceManager.isProxyActivated = proxyCheckBox.isSelected
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

    fun openPingTab(pingAddresses: List<String>) {
        if (pingController != null) {
            closePingTab()
        }
        val fxmlLoader = FXMLLoader(TCPConfigApp::class.java.getResource("ping-view.fxml"))
        val node = fxmlLoader.load<BorderPane>()
        node.prefHeightProperty().bind(splitPane.heightProperty())
        pingController = fxmlLoader.getController<PingController>()
        pingController!!.mainController = this
        pingController!!.pingAddresses = pingAddresses
        splitPane.items.add(node)
        splitPane.setDividerPositions(1 - PING_TAB_SIZE_RATIO, PING_TAB_SIZE_RATIO)
        pingController!!.doPings()
    }

    fun closePingTab() {
        splitPane.items.removeLastOrNull()
        splitPane.setDividerPositions(1.0)
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
        fun addDarkenEffect(button: Button) {
            val colorAdjust = ColorAdjust()
            colorAdjust.brightness = 0.0
            button.effect = colorAdjust
            button.setOnMouseEntered {
                val fadeInTimeline = Timeline(
                    KeyFrame(
                        Duration.seconds(0.0),
                        KeyValue(
                            colorAdjust.brightnessProperty(),
                            colorAdjust.brightnessProperty().value,
                            Interpolator.LINEAR,
                        ),
                    ),
                    KeyFrame(Duration.seconds(0.1), KeyValue(colorAdjust.brightnessProperty(), -0.2, Interpolator.LINEAR))
                )
                fadeInTimeline.cycleCount = 1
                fadeInTimeline.isAutoReverse = false
                fadeInTimeline.play()
            }
            button.setOnMouseExited {
                colorAdjust.brightnessProperty().value = 0.0
                button.effect = colorAdjust
            }
        }
    }
}
