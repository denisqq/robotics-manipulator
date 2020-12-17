package ru.psu.view

import javafx.geometry.Orientation
import javafx.geometry.Point2D
import javafx.geometry.Pos
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import javafx.scene.shape.Circle
import javafx.scene.shape.Line
import ru.psu.controller.impl.ChainControllerImpl
import ru.psu.model.*
import ru.psu.model.enums.ChainElementType
import tornadofx.*

class MainView : View("MainView") {
    private val chainController = ChainControllerImpl()
    private var workArea: Pane by singleAssign()

    private var chain = chainController.getChain().copy()
    private val segments = mutableListOf<Line>()

    private var selectedSegment: Line? = null
    private var selectedOffset: Point2D? = null

    init {
        chain.rootElement?.let { drawChain(it) }
    }

    override val root = borderpane {
        setPrefSize(1280.0, 1024.0)
//                fun createSegment(): Line {
//                    val startX: Double
//                    val startY: Double
//                    if (segments.isEmpty()) {
//                        startX = 10.0
//                        startY = 10.0
//                    } else {
//                        startX = segments.last().endX
//                        startY = segments.last().endY
//                    }
//                    val endX = startX.plus(Random.nextDouble(50.0))
//                    val endY = startY.plus(Random.nextDouble(50.0))
//                    return line(startX, startY, endX, endY) {
//                        strokeWidth = 5.0
//                        segments.add(this)
//                    }
//                }
        left {
            workArea = pane {

                addEventFilter(MouseEvent.MOUSE_PRESSED, ::startDrag)
                addEventFilter(MouseEvent.MOUSE_DRAGGED, ::drag)
                addEventFilter(MouseEvent.MOUSE_RELEASED, ::endDrag)
            }

            paddingAll = 10.0
        }
        right {
            form {
                fieldset("Сегмент") {
                    field("Вес:") {
                        textfield { }
                    }
                    field("Координаты начала:", Orientation.VERTICAL) {
                        text("X")
                        text("Y")
                    }
                    field("Координаты конца:", Orientation.VERTICAL) {
                        text("X")
                        text("Y")
                    }
                    hbox(spacing = 50.0 ,alignment = Pos.CENTER) {
                        button("Создать сегмент") {
//                        action { createSegment() }
                        }
                        button("Удалить сегмент") {
//                        action { createJoint() }
                        }
                    }
                }
                fieldset("Сустав") {
                    field("Вес:") {
                        textfield { }
                    }
                    field("Максимальный угол:") {
                        textfield { }
                    }
                    field("Координаты центра:", Orientation.VERTICAL) {
                        text("X")
                        text("Y")
                    }
                    hbox(spacing = 50.0 ,alignment = Pos.CENTER) {
                        button("Создать сустав") {
//                        action { createJoint() }
                        }
                        button("Удалить сустав") {
//                        action { createJoint() }
                        }
                    }
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

    private fun drawChain(rootChainElement: ChainElement) {
        when (rootChainElement.elementType) {
            ChainElementType.SEGMENT -> {
                rootChainElement as ChainSegment
                rootChainElement.segmentJoint?.let { drawJoint(it) }
            }
            ChainElementType.JOINT -> {
                rootChainElement as SegmentJoint
                rootChainElement.segments.forEach { drawSegment(it) }
            }
        }
    }

    private fun drawJoint(joint: SegmentJoint) {
        val centerPoint = joint.point
        val circle = Circle(centerPoint.x, centerPoint.y, 5.0)
        workArea += circle
    }

    private fun drawSegment(segment: ChainSegment) {
        val startPoint = segment.startPoint
        val endPoint = segment.endPoint
        val line = Line(startPoint.x, startPoint.y, endPoint.x, endPoint.y)
        line.strokeWidth = 5.0
        workArea += line
    }

    private fun createSegment(weight: Double, endPoint: Point, startPoint: Point, rootElement: ChainElement) {
        val segment = ChainSegment(null, weight, SystemCoordinate(1337.0), startPoint, endPoint)
        this.chain = chainController.addChainElement(segment, rootElement)
        this.chain.rootElement?.let { drawChain(it) }
    }

    private fun createJoint(weight: Double, point: Point, maxAngle: Double, rootElement: ChainElement) {
        val joint = SegmentJoint(null, weight, SystemCoordinate(228.0), point, maxAngle, null)
        this.chain = chainController.addChainElement(joint, rootElement)
        this.chain.rootElement?.let { drawChain(it) }
    }
}

