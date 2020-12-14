package ru.psu.view

import javafx.geometry.Point2D
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.shape.Line
import tornadofx.*
import kotlin.random.Random

class MainView : View("MainView") {
    private var workArea: Pane by singleAssign()

    private val segments = mutableListOf<Line>()

    private var selectedSegment: Line? = null
    private var selectedOffset: Point2D? = null

    override val root = borderpane {
        setPrefSize(1280.0, 1024.0)
                fun createSegment(): Line {
                    val startX: Double
                    val startY: Double
                    if (segments.isEmpty()) {
                        startX = 10.0
                        startY = 10.0
                    } else {
                        startX = segments.last().endX
                        startY = segments.last().endY
                    }
                    val endX = startX.plus(Random.nextDouble(50.0))
                    val endY = startY.plus(Random.nextDouble(50.0))
                    return line(startX, startY, endX, endY) {
                        strokeWidth = 5.0
                        segments.add(this)
                    }
                }
        left {
            workArea = pane {

                addEventFilter(MouseEvent.MOUSE_PRESSED, ::startDrag)
                addEventFilter(MouseEvent.MOUSE_DRAGGED, ::drag)
                addEventFilter(MouseEvent.MOUSE_RELEASED, ::endDrag)
            }

            paddingAll = 10.0
        }
        right {
            hbox {
                button("Create segment") {
                    action { workArea += createSegment() }
                }
            }
        }
    }

    private fun startDrag(evt: MouseEvent) {
        segments.firstOrNull {
            val mousePt = it.sceneToLocal(evt.sceneX, evt.sceneY)
            it.contains(mousePt)
        }
            .apply {
                if (this != null) {

                    selectedSegment = this

                    val mp = this.parent.sceneToLocal(evt.sceneX, evt.sceneY)
                    val vizBounds = this.boundsInParent

                    selectedOffset = Point2D(
                        mp.x - vizBounds.minX - (vizBounds.width - this.boundsInLocal.width) / 2,
                        mp.y - vizBounds.minY - (vizBounds.height - this.boundsInLocal.height) / 2
                    )
                }
            }
    }

    private fun drag(evt: MouseEvent) {
        val mousePt: Point2D = (evt.source as Pane).sceneToLocal(evt.sceneX, evt.sceneY)
        if (selectedSegment != null && selectedOffset != null) {
            selectedSegment!!.endX = mousePt.x
            selectedSegment!!.endY = mousePt.y
            val index = segments.indexOf(selectedSegment) + 1
            if (segments.size > index) {
                segments[index].startX = mousePt.x
                segments[index].startY = mousePt.y
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun endDrag(evt: MouseEvent) {
        selectedSegment = null
        selectedOffset = null
    }
}

