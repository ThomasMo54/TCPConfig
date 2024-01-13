package com.motompro.tcpconfig.app.component

import com.motompro.tcpconfig.app.TCPConfigApp
import com.motompro.tcpconfig.app.controller.PingController
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.geometry.Insets
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.BorderPane
import javafx.scene.layout.CornerRadii
import javafx.scene.paint.Color
import javafx.scene.text.Text
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.net.InetAddress

private val PING_FAIL_COLOR = Background(BackgroundFill(Color.color(1.0, 0.75, 0.75), CornerRadii.EMPTY, Insets.EMPTY))
private val PING_SUCCESS_COLOR = Background(BackgroundFill(Color.color(0.75, 1.0, 0.75), CornerRadii.EMPTY, Insets.EMPTY))

private const val LIVE_ICON_PATH = "image/live-icon.png"
private const val NOT_LIVE_ICON_PATH = "image/not-live-icon.png"

private const val NEW_ATTEMPT_DELAY = 1000L

class PingComponent {

    lateinit var address: String
    var result: Boolean = false
    lateinit var componentPane: BorderPane

    private var isLive = false
    private var liveJob: Job? = null

    @FXML
    private lateinit var addressText: Text
    @FXML
    private lateinit var liveText: Text
    @FXML
    private lateinit var liveIcon: ImageView

    @FXML
    private fun onLiveButtonClick() {
        if (isLive) {
            liveText.isVisible = false
            liveIcon.image = Image(TCPConfigApp::class.java.getResourceAsStream(NOT_LIVE_ICON_PATH))
            stopLive()
        } else {
            liveText.isVisible = true
            liveIcon.image = Image(TCPConfigApp::class.java.getResourceAsStream(LIVE_ICON_PATH))
            startLive()
        }
        isLive = !isLive
    }

    private fun startLive() {
        liveJob = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                try {
                    val address = InetAddress.getByName(address)
                    val result = address.isReachable(PingController.PING_TIMEOUT)
                    Platform.runLater { componentPane.background = if (result) PING_SUCCESS_COLOR else PING_FAIL_COLOR }
                } catch (_: Exception) {
                    Platform.runLater { componentPane.background = PING_FAIL_COLOR }
                }
                delay(NEW_ATTEMPT_DELAY)
            }
        }
    }

    fun show() {
        addressText.text = address
        componentPane.background = if (result) PING_SUCCESS_COLOR else PING_FAIL_COLOR
    }

    fun stopLive() {
        if (!isLive) return
        liveJob?.cancel()
    }
}
