package com.motompro.tcpconfig.app.controller

import com.motompro.tcpconfig.app.TCPConfigApp
import com.motompro.tcpconfig.app.component.RangeComponent
import com.motompro.tcpconfig.app.util.IPRange
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Button
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox

class CreatePingController {

    private val ranges = mutableListOf<RangeComponent>()

    @FXML
    private lateinit var backButton: Button
    @FXML
    private lateinit var rangesVBox: VBox

    @FXML
    private fun initialize() {
        MainController.addDarkenEffect(backButton)
        addNewRange()
    }

    @FXML
    private fun onBackButtonClick(event: ActionEvent) {
        TCPConfigApp.INSTANCE.swapScene("main-view.fxml")
    }

    @FXML
    private fun onAddRangeButtonClick() {
        addNewRange()
    }

    @FXML
    private fun onRemoveRangeButtonClick() {
        removeLastRange()
    }

    @FXML
    private fun onValidateButtonClick() {
        for (i in 0 until ranges.size) {
            if (!ranges[i].range.isValid) {
                TCPConfigApp.INSTANCE.showErrorAlert("Erreur", "La plage ${i + 1} contient une adresse mal formatée.")
                return
            }
        }
        if (ranges.size > 1) {
            for (i in 0 until ranges.size) {
                for (j in (i + 1) until ranges.size) {
                    if (ranges[i].range.isOverlapping(ranges[j].range)) {
                        TCPConfigApp.INSTANCE.showErrorAlert("Erreur", "Les plages ${i + 1} et ${j + 1} se chevauchent.")
                        return
                    }
                }
            }
        }
        val ipList = ranges
            .map { it.range }
            .flatMap { it.ipList }
            .sortedWith { ip1, ip2 -> if (IPRange.isIPSmallerThanOther(ip1, ip2)) -1 else 1 }
        val mainController = TCPConfigApp.INSTANCE.swapScene<MainController>("main-view.fxml")
        mainController.openPingTab(ipList)
    }

    private fun addNewRange() {
        val fxmlLoader = FXMLLoader(TCPConfigApp::class.java.getResource("range-component.fxml"))
        val node = fxmlLoader.load<HBox>()
        val controller = fxmlLoader.getController<RangeComponent>()
        rangesVBox.children.add(node)
        ranges.add(controller)
    }

    private fun removeLastRange() {
        rangesVBox.children.removeLastOrNull()
        ranges.removeLastOrNull()
    }
}
