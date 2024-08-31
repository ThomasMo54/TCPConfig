package com.motompro.tcpconfig.app.component.draggabletab

import javafx.scene.control.Label
import javafx.scene.control.Tab
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.ClipboardContent
import javafx.scene.input.Dragboard
import javafx.scene.input.TransferMode
import javafx.scene.layout.HBox

class DraggableTab(
    labelText: String
) : Tab() {

    var id: Int = -1

    private val iconLabelPane = HBox()
    private val icon = ImageView()
    private val label = Label()

    var labelText: String = ""
        set(value) {
            label.text = value
            field = value
        }

    constructor() : this("")

    init {
        text = null

        // Setup label
        label.text = labelText

        // Setup icon
        icon.fitWidth = 16.0
        icon.fitHeight = 16.0

        // Setup containers
        iconLabelPane.spacing = 5.0
        iconLabelPane.children.add(icon)
        iconLabelPane.children.add(label)
        graphic = iconLabelPane

        // Setup drag behavior
        iconLabelPane.setOnDragDetected { event ->
            val dragboard: Dragboard = iconLabelPane.startDragAndDrop(TransferMode.MOVE)
            val content = ClipboardContent()
            content.putString(id.toString())
            dragboard.setContent(content)
            dragboard.dragView = iconLabelPane.snapshot(null, null)
            event.consume()
        }

        iconLabelPane.setOnDragDropped { event ->
            event.consume()
        }
    }

    fun setIcon(image: Image?) {
        icon.image = image
    }
}