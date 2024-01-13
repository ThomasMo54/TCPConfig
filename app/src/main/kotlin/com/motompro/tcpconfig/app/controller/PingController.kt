package com.motompro.tcpconfig.app.controller

import com.motompro.tcpconfig.app.TCPConfigApp
import com.motompro.tcpconfig.app.component.PingComponent
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Button
import javafx.scene.control.ProgressBar
import javafx.scene.control.ScrollPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import java.net.InetAddress

private const val THREAD_POOL_SIZE = 16

class PingController {

    lateinit var mainController: MainController
    lateinit var pingAddresses: List<String>

    private val pingJobs = mutableSetOf<Job>()
    private var isClosed = false
    private val showedPings = mutableListOf<Int>()
    private val pingComponents = mutableSetOf<PingComponent>()

    @FXML
    private lateinit var scrollPane: ScrollPane
    @FXML
    private lateinit var closeButton: Button
    @FXML
    private lateinit var restartButton: Button
    @FXML
    private lateinit var progressBar: ProgressBar
    @FXML
    private lateinit var progressText: Text
    @FXML
    private lateinit var pingResultsVBox: VBox

    @FXML
    private fun initialize() {
        MainController.addDarkenEffect(closeButton)
        MainController.addDarkenEffect(restartButton)
    }

    @FXML
    private fun onCloseButtonClick() {
        isClosed = true
        pingJobs.forEach { it.cancel() }
        pingComponents.forEach { it.stopLive() }
        mainController.closePingTab()
    }

    @FXML
    private fun onRestartButtonClick() {
        if (showedPings.size != pingAddresses.size) {
            TCPConfigApp.INSTANCE.showErrorAlert("Erreur", "Veuillez attendre que les pings soient terminés.")
            return
        }
        pingJobs.forEach { it.cancel() }
        pingComponents.forEach { it.stopLive() }
        pingJobs.clear()
        pingComponents.clear()
        showedPings.clear()
        pingResultsVBox.children.clear()
        doPings()
    }

    fun doPings() {
        progressBar.progress = 0.0
        progressBar.isVisible = true
        progressText.text = "0/${pingAddresses.size}"
        @OptIn(DelicateCoroutinesApi::class)
        val pingDispatcher = newFixedThreadPoolContext(THREAD_POOL_SIZE, "Pings")
        for (i in pingAddresses.indices) {
            val addressName = pingAddresses[i]
            pingJobs.add(CoroutineScope(pingDispatcher).launch {
                try {
                    val address = InetAddress.getByName(addressName)
                    val result = address.isReachable(PING_TIMEOUT)
                    showPingResult(addressName, i, result)
                } catch (_: Exception) {
                    showPingResult(addressName, i, false)
                }
            })
        }
    }

    private fun showPingResult(address: String, index: Int, result: Boolean) {
        if (isClosed) return
        Platform.runLater {
            var childIndex = -1
            if (showedPings.isEmpty()) {
                childIndex = 0
            } else {
                for (i in showedPings.indices) {
                    if (index < showedPings[i]) {
                        childIndex = i
                        break
                    }
                }
                if (childIndex == -1) childIndex = showedPings.size
            }
            val pingComponentLoader = FXMLLoader(TCPConfigApp.INSTANCE.getResourceOrLoad("ping-component.fxml"))
            val pingComponent = pingComponentLoader.load<BorderPane>()
            pingComponent.prefWidthProperty().bind((scrollPane.parent as BorderPane).widthProperty())
            val componentController = pingComponentLoader.getController<PingComponent>()
            pingComponents.add(componentController)
            componentController.address = address
            componentController.result = result
            componentController.componentPane = pingComponent
            pingResultsVBox.children.add(childIndex, pingComponent)
            showedPings.add(childIndex, index)
            componentController.show()
            if (showedPings.size == pingAddresses.size) {
                progressBar.isVisible = false
            } else {
                progressBar.progress = showedPings.size.toDouble() / pingAddresses.size
            }
            progressText.text = "${showedPings.size}/${pingAddresses.size}"
        }
    }

    companion object {
        const val PING_TIMEOUT = 5000
    }
}
