package com.motompro.tcpconfig.app.controller

import com.motompro.tcpconfig.app.TCPConfigApp
import com.motompro.tcpconfig.app.config.Config
import javafx.animation.Interpolator
import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.effect.ColorAdjust
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.BorderPane
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.util.Duration
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
    private fun initialize() {
        updateConfigList()
        searchTextField.textProperty().addListener { _, _, newValue ->
            currentSearch = newValue
            updateConfigList()
        }
    }

    @FXML
    private fun onAddConfigButtonClick(event: ActionEvent) {
        TCPConfigApp.INSTANCE.swapScene("add-edit-config-view.fxml")
    }

    @FXML
    private fun onWebsiteHyperlinkClick(event: ActionEvent) {
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
        val fxmlLoader = FXMLLoader(TCPConfigApp::class.java.getResource("config-view.fxml"))
        val node = fxmlLoader.load<BorderPane>()
        val controller = fxmlLoader.getController<ConfigController>()
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
                    KeyFrame(Duration.seconds(0.2), KeyValue(colorAdjust.brightnessProperty(), -0.2, Interpolator.LINEAR))
                )
                fadeInTimeline.cycleCount = 1
                fadeInTimeline.isAutoReverse = false
                fadeInTimeline.play()
            }
            button.setOnMouseExited {
                val fadeOutTimeline = Timeline(
                    KeyFrame(
                        Duration.seconds(0.0),
                        KeyValue(
                            colorAdjust.brightnessProperty(),
                            colorAdjust.brightnessProperty().value,
                            Interpolator.LINEAR,
                        ),
                    ),
                    KeyFrame(Duration.seconds(0.2), KeyValue(colorAdjust.brightnessProperty(), 0, Interpolator.LINEAR))
                )
                fadeOutTimeline.cycleCount = 1
                fadeOutTimeline.isAutoReverse = false
                fadeOutTimeline.play()
            }
        }
    }
}
