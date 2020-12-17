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

    private var chain = chainController.getChain().copy()
    private var rootElement = chain.rootElement
    private val segments = mutableListOf<Line>()

    private var selectedSegment: Line? = null
    private var selectedOffset: Point2D? = null
    private var activeSegmentButton = false
    private var activeJointButton = false

    private var currentSegment: ChainSegment? = null
    private var currentJoint: SegmentJoint? = null
    private var currentElement: ChainElement? = null
    private var currentSegmentWeight: Double? = null
    private var currentJointWeight: Double? = null
    private var currentMaxAngle: Double? = null

    init {
        rootElement?.let { drawChain(it) }
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
                        textfield {
                            textProperty().addListener { observable, oldValue, newValue ->
                                currentSegmentWeight = newValue.toDoubleOrNull().apply { activeSegmentButton = true }
//                                if (currentWeight != null) {
//                                        activeSegmentButton = true
//                                    }
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
                            textProperty().addListener { observable, oldValue, newValue ->
                                currentJointWeight = newValue.toDoubleOrNull().apply { activeJointButton = true }
//                                if (currentWeight != null) {
//                                    activeJointButton = true
//                                }
                            }
                            this.text = (currentElement as SegmentJoint?)?.weight.toString().let { "" }
                        }
                    }
                    field("Максимальный угол:") {
                        textfield {
                            textProperty().addListener { observable, oldValue, newValue ->
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
                drawSegment(rootChainElement)
                rootChainElement.childSegmentJoint?.let { drawChain(it) }
            }
            ChainElementType.JOINT -> {
                rootChainElement as SegmentJoint
                drawJoint(rootChainElement)
                rootChainElement.childSegments.forEach { drawChain(it) }
            }
        }
    }

    private fun drawJoint(joint: SegmentJoint) {
        this.currentElement = joint
        val centerPoint = joint.point
        val circle = Circle(centerPoint.x, centerPoint.y, 5.0)
        workArea += circle
    }

    private fun drawSegment(segment: ChainSegment) {
        this.currentElement = segment
        val startPoint = segment.startPoint
        val endPoint = segment.endPoint
        val line = Line(startPoint.x, startPoint.y, endPoint.x, endPoint.y)
        line.strokeWidth = 5.0
        workArea += line
    }

    private fun createSegment(weight: Double) {
        workArea.clear()
        val startPoint = if (currentElement == null) {
            Point(0.0, 0.0)
        } else {
            Point((currentElement as SegmentJoint).point.x, (currentElement as SegmentJoint).point.y)
        }
        val endPoint =
            Point(startPoint.x.plus(Random.nextDouble(10.0, 100.0)), startPoint.y.plus(Random.nextDouble(10.0, 100.0)))
        val segment = ChainSegment(null, weight, SystemCoordinate(1337.0), endPoint, startPoint)
        val chain = chainController.addChainElement(segment, currentElement).copy()
        this.chain = chain
        this.rootElement = chain.rootElement
        this.rootElement?.let { drawChain(it) }
    }

    private fun createJoint(weight: Double, maxAngle: Double) {
        workArea.clear()
        val point = if (currentElement == null) {
            Point(0.0, 0.0)
        } else {
            Point((currentElement as ChainSegment).endPoint.x, (currentElement as ChainSegment).endPoint.y)
        }
        val joint = SegmentJoint(null, weight, SystemCoordinate(228.0), point, maxAngle, null)
        val chain = chainController.addChainElement(joint, currentElement).copy()
        this.chain = chain
        this.rootElement = chain.rootElement
        this.rootElement?.let { drawChain(it) }
    }

    private fun deleteElement() {
        workArea.clear()
        val chain = chainController.deleteChainElement(currentElement!!)
        this.chain = chain
        this.rootElement = chain.rootElement
        this.currentElement = null
        this.rootElement?.let { drawChain(it) }
    }
}

