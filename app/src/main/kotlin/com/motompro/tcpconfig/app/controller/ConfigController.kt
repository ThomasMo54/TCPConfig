package com.motompro.tcpconfig.app.controller

import com.motompro.tcpconfig.app.config.Config
import javafx.animation.Interpolator
import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.effect.ColorAdjust
import javafx.scene.input.MouseEvent
import javafx.scene.text.Text
import javafx.util.Duration


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
        // Set buttons hover animation
        buttons.forEach { button ->
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
