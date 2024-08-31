package com.motompro.tcpconfig.app.controller

import com.motompro.tcpconfig.app.TCPConfigApp
import com.motompro.tcpconfig.app.component.draggabletab.DraggableTab
import com.motompro.tcpconfig.app.component.draggabletab.SplitTabPane
import com.motompro.tcpconfig.app.config.Config
import com.motompro.tcpconfig.app.util.IPRange
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane

class MainController {

    @FXML
    private lateinit var mainPane: BorderPane

    @FXML
    private lateinit var tcpConfigButton: Button
    @FXML
    private lateinit var dhcpConfigButton: Button
    @FXML
    private lateinit var pingButton: Button
    @FXML
    private lateinit var proxyButton: Button

    private val tabPane = SplitTabPane()
    private val buttons by lazy { listOf(tcpConfigButton, dhcpConfigButton, pingButton, proxyButton) }
    private var proxyEnabled = false
        set(value) {
            field = value
            setButtonToggled(proxyButton, value)
        }
    var tcpController: TCPController? = null
    var dhcpController: DHCPController? = null

    @FXML
    private fun initialize() {
        mainPane.center = tabPane
        buttons.forEach { setButtonHoverEffect(it) }
        proxyEnabled = TCPConfigApp.INSTANCE.netInterfaceManager.isProxyActivated
        createTCPTab()
    }

    @FXML
    private fun onTCPButtonClick() {
        createTCPTab()
    }

    @FXML
    private fun onDHCPButtonClick() {
        createDHCPTab()
    }

    @FXML
    private fun onPingButtonClick() {
        createPingCreationTab()
    }

    @FXML
    private fun onProxyButtonClick() {
        if (proxyEnabled) {
            TCPConfigApp.INSTANCE.netInterfaceManager.isProxyActivated = false
            proxyEnabled = false
        } else {
            TCPConfigApp.INSTANCE.netInterfaceManager.isProxyActivated = true
            proxyEnabled = true
        }
    }

    fun createTCPTab() {
        if (tcpController != null) {
            tabPane.select(tcpController!!.tab)
            return
        }
        val fxmlLoader = FXMLLoader(TCPConfigApp::class.java.getResource("tcp-view.fxml"))
        val content = fxmlLoader.load<Pane>()
        val icon = Image(TCPConfigApp::class.java.getResourceAsStream("image/tcp-configuration-icon.png"))
        val tab = addTab(icon, "Config TCP", content)
        tcpController = fxmlLoader.getController<TCPController>()
        tcpController!!.tab = tab
        tabPane.select(tab)
        setButtonToggled(tcpConfigButton, true)
        tab.setOnClosed {
            tcpController = null
            setButtonToggled(tcpConfigButton, false)
        }
    }

    fun createDHCPTab() {
        if (dhcpController != null) {
            tabPane.select(dhcpController!!.tab)
            return
        }
        val fxmlLoader = FXMLLoader(TCPConfigApp::class.java.getResource("dhcp-view.fxml"))
        val content = fxmlLoader.load<Pane>()
        val icon = Image(TCPConfigApp::class.java.getResourceAsStream("image/dhcp-configuration-icon.png"))
        val tab = addTab(icon, "Config DHCP", content)
        dhcpController = fxmlLoader.getController<DHCPController>()
        dhcpController!!.tab = tab
        tabPane.select(tab)
        setButtonToggled(dhcpConfigButton, true)
        tab.setOnClosed {
            dhcpController = null
            setButtonToggled(dhcpConfigButton, false)
        }
    }

    fun createNewConfigTab(focus: Boolean = true) {
        val fxmlLoader = FXMLLoader(TCPConfigApp::class.java.getResource("add-edit-config-view.fxml"))
        val content = fxmlLoader.load<Pane>()
        val controller = fxmlLoader.getController<AddEditConfigController>()
        val icon = Image(TCPConfigApp::class.java.getResourceAsStream("image/tcp-configuration-icon.png"))
        val tab = addTab(icon, "Nouvelle config", content)
        controller.tab = tab
        if (focus) {
            tabPane.select(tab)
        }
    }

    fun createEditConfigTab(config: Config, focus: Boolean = true) {
        val fxmlLoader = FXMLLoader(TCPConfigApp::class.java.getResource("add-edit-config-view.fxml"))
        val content = fxmlLoader.load<Pane>()
        val controller = fxmlLoader.getController<AddEditConfigController>()
        controller.configInEdition = config
        val icon = Image(TCPConfigApp::class.java.getResourceAsStream("image/tcp-configuration-icon.png"))
        val tab = addTab(icon, "Éditer \"${config.name}\"", content)
        controller.tab = tab
        if (focus) {
            tabPane.select(tab)
        }
    }

    fun createPingCreationTab(focus: Boolean = true) {
        val fxmlLoader = FXMLLoader(TCPConfigApp::class.java.getResource("create-ping-view.fxml"))
        val content = fxmlLoader.load<Pane>()
        val icon = Image(TCPConfigApp::class.java.getResourceAsStream("image/ping-icon-2.png"))
        val tab = addTab(icon, "Nouveau ping", content)
        val controller = fxmlLoader.getController<CreatePingController>()
        controller.tab = tab
        if (focus) {
            tabPane.select(tab)
        }
    }

    fun createPingResultTab(focus: Boolean = true, pingAddresses: List<String>) {
        val fxmlLoader = FXMLLoader(TCPConfigApp::class.java.getResource("ping-view.fxml"))
        val content = fxmlLoader.load<Pane>()
        val icon = Image(TCPConfigApp::class.java.getResourceAsStream("image/ping-icon-2.png"))
        val tab = addTab(icon, "Ping ${pingAddresses[0]}", content)
        val controller = fxmlLoader.getController<PingController>()
        controller.pingAddresses = pingAddresses
        controller.doPings()
        if (focus) {
            tabPane.select(tab)
        }
    }

    fun addTab(icon: Image?, title: String, content: Pane): DraggableTab {
        val tab = DraggableTab(title)
        tab.setIcon(icon)
        tab.content = content
        tabPane.addTab(tab)
        content.prefWidthProperty().bind(tab.tabPane.widthProperty())
        content.prefHeightProperty().bind(tab.tabPane.heightProperty())
        return tab
    }

    fun closeTab(tab: DraggableTab) {
        tabPane.closeTab(tab)
    }

    fun focusTCPTab() {
        if (tcpController != null) {
            tabPane.select(tcpController!!.tab)
        }
    }

    companion object {
        private const val TRANSPARENT = "transparent"
        private const val BUTTON_HOVER_COLOR = "#eaeaea"
        private const val BUTTON_TOGGLED_COLOR = "#e0e0e0"
        private const val BUTTON_HOVER_TOGGLED_COLOR = "#dadada"

        fun setButtonHoverEffect(button: Button) {
            button.setOnMouseEntered { button.style = button.style.replace(TRANSPARENT, BUTTON_HOVER_COLOR).replace(BUTTON_TOGGLED_COLOR, BUTTON_HOVER_TOGGLED_COLOR) }
            button.setOnMouseExited { button.style = button.style.replace(BUTTON_HOVER_COLOR, TRANSPARENT).replace(BUTTON_HOVER_TOGGLED_COLOR, BUTTON_TOGGLED_COLOR) }
        }

        fun setButtonToggled(button: Button, toggled: Boolean) {
            if (toggled) {
                button.style = button.style.replace(TRANSPARENT, BUTTON_TOGGLED_COLOR).replace(BUTTON_HOVER_COLOR, BUTTON_TOGGLED_COLOR).replace(BUTTON_HOVER_TOGGLED_COLOR, BUTTON_TOGGLED_COLOR)
            } else {
                button.style = button.style.replace(BUTTON_TOGGLED_COLOR, TRANSPARENT).replace(BUTTON_HOVER_TOGGLED_COLOR, TRANSPARENT)
            }
        }
    }
}