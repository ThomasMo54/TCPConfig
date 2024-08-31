package com.motompro.tcpconfig.app.component.draggabletab

import javafx.geometry.Orientation
import javafx.scene.control.SplitPane
import javafx.scene.control.TabPane
import javafx.scene.input.TransferMode

class SplitTabPane : SplitPane() {

    var tabPane: TabPane = TabPane()
        set(value) {
            items.clear()
            field = value
            items.add(value)
        }

    val isSplit: Boolean
        get() = firstChildSplitTabPane != null && secondChildSplitTabPane != null

    private var firstChildSplitTabPane: SplitTabPane? = null
    private var secondChildSplitTabPane: SplitTabPane? = null

    init {
        items.add(tabPane)
        tabPane.tabClosingPolicy = TabPane.TabClosingPolicy.ALL_TABS

        setOnDragOver { event ->
            if (isSplit) {
                return@setOnDragOver
            }
            if (event.dragboard.hasString() && tabs.size > 1) {
                event.acceptTransferModes(TransferMode.MOVE)
            }
            event.consume()
        }

        setOnDragDropped { event ->
            // Get the tab to split
            val id = event.dragboard.string.toInt()
            val tab = tabs[id] ?: return@setOnDragDropped

            // Get the relative position of the drop
            val relativeX = event.x / width
            val relativeY = event.y / height

            if (relativeY <= 0.2 && !tabPane.tabs.contains(tab)) {
                if (tab.tabPane.parent?.parent is SplitTabPane) {
                    (tab.tabPane.parent.parent as SplitTabPane).mergePanesIfNeeded(tab.tabPane)
                    tab.tabPane?.tabs?.remove(tab)
                }
                tabPane.tabs.add(tab)
                event.isDropCompleted = true
                event.consume()
                return@setOnDragDropped
            }

            // Split only if not already split
            if (isSplit) {
                return@setOnDragDropped
            }

            // Split the panes
            if (relativeX <= 0.2) {
                orientation = Orientation.HORIZONTAL
                splitPanes(tab, true)
            } else if (relativeX >= 0.8) {
                orientation = Orientation.HORIZONTAL
                splitPanes(tab, false)
            } else if (relativeY >= 0.8) {
                orientation = Orientation.VERTICAL
                splitPanes(tab, false)
            }

            event.isDropCompleted = true
            event.consume()
        }
    }

    private fun splitPanes(tab: DraggableTab, putTabInFirst: Boolean) {
        firstChildSplitTabPane = SplitTabPane()
        secondChildSplitTabPane = SplitTabPane()

        if (!mergePanesIfNeeded(tab.tabPane)) {
            tab.tabPane.tabs.remove(tab)
        }

        if (putTabInFirst) {
            secondChildSplitTabPane!!.tabPane = tabPane
            firstChildSplitTabPane!!.tabPane.tabs.add(tab)
        } else {
            firstChildSplitTabPane!!.tabPane = tabPane
            secondChildSplitTabPane!!.tabPane.tabs.add(tab)
        }

        items.clear()
        items.addAll(firstChildSplitTabPane, secondChildSplitTabPane)
    }

    /**
     * Merge the panes if the pane has only one tab
     * @param pane the tab pane to check
     * @return true if the panes were merged, false otherwise
     */
    private fun mergePanesIfNeeded(pane: TabPane): Boolean {
        if (pane.tabs.size <= 1 && pane.parent != null && pane.parent?.parent?.parent?.parent is SplitTabPane) {
            pane.tabs.clear()
            (pane.parent?.parent?.parent?.parent as SplitTabPane).mergePanes(pane.parent?.parent as SplitTabPane)
            return true
        }
        return false
    }

    fun mergePanes(paneToRemove: SplitTabPane) {
        items.remove(paneToRemove)
        if (items.isEmpty() && parent?.parent is SplitTabPane) {
            (parent?.parent as SplitTabPane).mergePanes(this)
        }
    }

    fun addTab(draggableTab: DraggableTab) {
        if (isSplit) {
            firstChildSplitTabPane!!.addTab(draggableTab)
            return
        }
        val id = idSequence.next()
        tabs[id] = draggableTab
        draggableTab.id = id
        tabPane.tabs.add(draggableTab)

        draggableTab.setOnCloseRequest {
            val parentPane = draggableTab.tabPane
            mergePanesIfNeeded(parentPane)
            tabs.remove(draggableTab.id)
        }
    }

    fun closeTab(tab: DraggableTab) {
        if (tabPane.tabs.contains(tab)) {
            tabPane.tabs.remove(tab)
            mergePanesIfNeeded(tabPane)
            tabs.remove(tab.id)
            return
        }
        firstChildSplitTabPane?.closeTab(tab)
        secondChildSplitTabPane?.closeTab(tab)
    }

    fun select(tab: DraggableTab) {
        if (tabPane.tabs.contains(tab)) {
            tabPane.selectionModel.select(tab)
            return
        }
        firstChildSplitTabPane?.select(tab)
        secondChildSplitTabPane?.select(tab)
    }

    companion object {
        private val tabs = mutableMapOf<Int, DraggableTab>()
        private val idSequence = generateSequence(0) { it + 1 }.iterator()
    }
}