package com.motompro.tcpconfig.app.component

import com.motompro.tcpconfig.app.util.IPRange
import javafx.fxml.FXML
import javafx.scene.control.TextField

class RangeComponent {

    @FXML
    lateinit var rangeStartTextField: TextField
    @FXML
    lateinit var rangeEndTextField: TextField

    val range: IPRange
        get() = IPRange(rangeStartTextField.text, rangeEndTextField.text)
}
