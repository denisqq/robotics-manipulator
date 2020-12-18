package ru.psu.view

import javafx.geometry.Orientation
import javafx.geometry.Point2D
import javafx.geometry.Pos
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.shape.Circle
import javafx.scene.shape.Line
import ru.psu.controller.impl.ChainControllerImpl
import ru.psu.model.*
import ru.psu.model.enums.ChainElementType
import tornadofx.*
import kotlin.random.Random

class MainView : View("MainView") {
    private val chainController = ChainControllerImpl()
    private var workArea: Pane by singleAssign()

    private var currentElement: ChainElement? = null
    private var currentSegmentWeight: Double? = null
    private var currentJointWeight: Double? = null
    private var currentMaxAngle: Double? = null

    init {
        val chain = chainController.getChain().copy()
        chain.rootElement?.let { drawAll(it) }
    }

    override val root = borderpane {
        setPrefSize(1280.0, 1024.0)
        left {
            workArea = pane {
                addEventFilter(MouseEvent.MOUSE_DRAGGED, ::dragElement)
                addEventFilter(MouseEvent.MOUSE_RELEASED, ::endDrag)
            }

            paddingAll = 10.0
        }
        right {
            form {
                fieldset("Сегмент") {
                    field("Вес:") {
                        textfield {
                            textProperty().addListener { _, _, newValue ->
                                currentSegmentWeight = newValue.toDoubleOrNull()
                            }
                            this.text = (currentElement as ChainSegment?)?.weight.toString().let { "" }
                        }
                    }
                    field("Координаты начала:", Orientation.VERTICAL) {
                        text("X") {
                            this.text = (currentElement as ChainSegment?)?.startPoint?.x.toString().let { "X" }
                        }
                        text("Y") {
                            this.text = (currentElement as ChainSegment?)?.startPoint?.x.toString().let { "Y" }
                        }
                    }
                    field("Координаты конца:", Orientation.VERTICAL) {
                        text("X") {
                            this.text = (currentElement as ChainSegment?)?.endPoint?.x.toString().let { "X" }
                        }
                        text("Y") {
                            this.text = (currentElement as ChainSegment?)?.endPoint?.y.toString().let { "Y" }
                        }
                    }
                    hbox(spacing = 50.0, alignment = Pos.CENTER) {
                        button("Создать сегмент") {
                            action { createSegment(currentSegmentWeight!!) }
//                            this.isDisable = true
                        }
                        button("Удалить сегмент") {
                            action { deleteElement() }
                        }
                    }
                }
                fieldset("Сустав") {
                    field("Вес:") {
                        textfield {
                            textProperty().addListener { _, _, newValue ->
                                currentJointWeight = newValue.toDoubleOrNull()
                            }
                            this.text = (currentElement as SegmentJoint?)?.weight.toString().let { "" }
                        }
                    }
                    field("Максимальный угол:") {
                        textfield {
                            textProperty().addListener { _, _, newValue ->
                                currentMaxAngle = newValue.toDoubleOrNull()
                            }
                            this.text = (currentElement as SegmentJoint?)?.maxAngle.toString().let { "" }
                        }
                    }
                    field("Координаты центра:", Orientation.VERTICAL) {
                        text("X") {
                            this.text = (currentElement as SegmentJoint?)?.point?.x.toString().let { "X" }
                        }
                        text("Y") {
                            this.text = (currentElement as SegmentJoint?)?.point?.y.toString().let { "Y" }
                        }
                    }
                    hbox(spacing = 50.0, alignment = Pos.CENTER) {
                        button("Создать сустав") {
                            action { createJoint(currentJointWeight!!, currentMaxAngle!!) }
                        }
                        button("Удалить сустав") {
                            action { deleteElement() }
                        }
                    }
                }
            }
        }
    }

    private fun drawChain(chain: Chain) {
        workArea.clear()
        chain.rootElement?.let { drawAll(it) }
    }

    private fun drawAll(rootChainElement: ChainElement) {
        when (rootChainElement.elementType) {
            ChainElementType.SEGMENT -> {
                rootChainElement as ChainSegment
                drawSegment(rootChainElement)
                rootChainElement.childSegmentJoint?.let { drawAll(it) }
            }
            ChainElementType.JOINT -> {
                rootChainElement as SegmentJoint
                drawJoint(rootChainElement)
                rootChainElement.childSegments.forEach { drawAll(it) }
            }
        }
    }

    private fun drawJoint(joint: SegmentJoint) {
        this.currentElement = joint
        val centerPoint = joint.point
        val circle = Circle(centerPoint.x, centerPoint.y, 10.0)
        circle.onLeftClick {
            this.currentElement = joint
        }
        circle.onHover {
            this.currentElement = joint
        }
        workArea += circle
    }

    private fun drawSegment(segment: ChainSegment) {
        this.currentElement = segment
        val startPoint = segment.startPoint
        val endPoint = segment.endPoint
        val line = Line(startPoint.x, startPoint.y, endPoint.x, endPoint.y)
        line.strokeWidth = 5.0
        line.onHover {
            this.currentElement = segment
        }
        line.onLeftClick {
            this.currentElement = segment
        }
        workArea += line
    }

    private fun createSegment(weight: Double) {
        val startPoint = if (currentElement == null) {
            Point(0.0, 0.0)
        } else {
            Point((currentElement as SegmentJoint).point.x, (currentElement as SegmentJoint).point.y)
        }
        val endPoint =
            Point(startPoint.x.plus(Random.nextDouble(10.0, 100.0)), startPoint.y.plus(Random.nextDouble(10.0, 100.0)))
        val segment = ChainSegment(null, weight, SystemCoordinate(1337.0), endPoint, startPoint)
        val chain = chainController.addChainElement(segment, currentElement).copy()
        drawChain(chain)
    }

    private fun createJoint(weight: Double, maxAngle: Double) {
        val point = if (currentElement == null) {
            Point(0.0, 0.0)
        } else {
            Point((currentElement as ChainSegment).endPoint.x, (currentElement as ChainSegment).endPoint.y)
        }
        val joint = SegmentJoint(null, weight, SystemCoordinate(228.0), point, maxAngle, null)
        val chain = chainController.addChainElement(joint, currentElement).copy()
        drawChain(chain)
    }

    private fun deleteElement() {
        val chain = chainController.deleteChainElement(currentElement!!).copy()
        drawChain(chain)
    }

    private fun dragElement(evt: MouseEvent) {
        val mousePoint: Point2D = (evt.source as Pane).sceneToLocal(evt.sceneX, evt.sceneY)
        if (currentElement != null) {
            when (currentElement!!.elementType) {
                ChainElementType.SEGMENT -> {
                    val segment = currentElement as ChainSegment
                    segment.endPoint.x = mousePoint.x
                    segment.endPoint.y = mousePoint.y
                    val chain = chainController.updateChainElement(segment.id!!, segment).copy()
                    drawChain(chain)
                }
                ChainElementType.JOINT -> {
                    val joint = currentElement as SegmentJoint
                    joint.point.x = mousePoint.x
                    joint.point.y = mousePoint.y
                    val chain = chainController.updateChainElement(joint.id!!, joint).copy()
                    drawChain(chain)
                }
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun endDrag(evt : MouseEvent) {
        currentElement = null
    }

}

