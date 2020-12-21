package ru.psu.view

import javafx.geometry.Orientation
import javafx.geometry.Point2D
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Line
import javafx.scene.shape.Shape
import ru.psu.controller.impl.ChainControllerImpl
import ru.psu.model.*
import ru.psu.model.enums.ChainElementType
import ru.psu.styles.Styles
import tornadofx.*
import kotlin.random.Random

class MainView : View("MainView") {
    private var workArea: Pane by singleAssign()
    private var createSegmentButton = Button("Создать сегмент")
    private var createJointButton = Button("Создать сустав")

    companion object {
        private var chainMap: MutableMap<ChainElement, Shape> = HashMap()

        private var selectedElement: ChainElement? = null
        private var selectedShape: Shape? = null
        private var lastElement: ChainElement? = null
        private var currentSegmentWeight: Double? = null
        private var currentJointWeight: Double? = null
        private var currentMaxAngle: Double? = null
    }

    init {
        setupButtons()
        val chain = ChainControllerImpl.getChain().copy()
        chain.rootElement?.let { drawAll(it) }
    }

    private fun setupButtons() {
        createSegmentButton.isDisable = true
        createSegmentButton.action { createSegment(currentSegmentWeight!!) }
        createJointButton.isDisable = true
        createJointButton.action { createJoint(currentJointWeight!!, currentMaxAngle!!) }
    }

    @Suppress("DuplicatedCode")
    override val root = borderpane {
        setPrefSize(1280.0, 1024.0)
        left {
            workArea = pane {
                addEventFilter(MouseEvent.MOUSE_PRESSED, ::chooseElement)
                addEventFilter(MouseEvent.MOUSE_DRAGGED, ::dragElement)
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
                                createSegmentButton.isDisable = currentSegmentWeight == null
                            }
                            this.text = (lastElement as ChainSegment?)?.weight.toString().let { "" }
                        }
                    }
                    hbox(spacing = 50.0, alignment = Pos.CENTER) {
                        this.addChildIfPossible(createSegmentButton)
                    }
                }
                fieldset("Сустав") {
                    field("Вес:") {
                        textfield {
                            textProperty().addListener { _, _, newValue ->
                                currentJointWeight = newValue.toDoubleOrNull()
                                createJointButton.isDisable = !(currentMaxAngle != null && currentJointWeight != null)
                            }
                            this.text = (lastElement as SegmentJoint?)?.weight.toString().let { "" }
                        }
                    }
                    field("Максимальный угол:") {
                        textfield {
                            textProperty().addListener { _, _, newValue ->
                                currentMaxAngle = newValue.toDoubleOrNull()
                                createJointButton.isDisable = !(currentMaxAngle != null && currentJointWeight != null)
                            }
                            this.text = (lastElement as SegmentJoint?)?.maxAngle.toString().let { "" }
                        }
                    }
                    hbox(spacing = 50.0, alignment = Pos.CENTER) {
                        this.addChildIfPossible(createJointButton)
                    }
                }
            }
        }
        bottom {
            hbox {
                when (lastElement?.elementType) {
                    ChainElementType.SEGMENT -> {
                        field("Координаты начала:", Orientation.VERTICAL) {
                            text("X") {
                                this.text = (lastElement as ChainSegment?)?.startPoint?.x.toString()
                            }
                            text("Y") {
                                this.text = (lastElement as ChainSegment?)?.startPoint?.x.toString()
                            }
                        }
                        field("Координаты конца:", Orientation.VERTICAL) {
                            text("X") {
                                this.text = (lastElement as ChainSegment?)?.endPoint?.x.toString()
                            }
                            text("Y") {
                                this.text = (lastElement as ChainSegment?)?.endPoint?.y.toString()
                            }
                        }
                    }
                    ChainElementType.JOINT -> {
                        field("Координаты центра:", Orientation.VERTICAL) {
                            text("X") {
                                this.text = (lastElement as SegmentJoint?)?.point?.x.toString()
                            }
                            text("Y") {
                                this.text = (lastElement as SegmentJoint?)?.point?.y.toString()
                            }
                        }
                    }
                }
                button("Удалить") {
                    action { deleteElement() }
                }
            }
        }
    }

    private fun drawChain(chain: Chain) {
        workArea.clear()
        chainMap.clear()
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
        val centerPoint = joint.point
        val circle = Circle(centerPoint.x, centerPoint.y, 10.0)
        chainMap[joint] = circle
        workArea += circle
    }

    private fun drawSegment(segment: ChainSegment) {
        val startPoint = segment.startPoint
        val endPoint = segment.endPoint
        val line = Line(startPoint.x, startPoint.y, endPoint.x, endPoint.y)
        line.strokeWidth = 5.0
        chainMap[segment] = line
        workArea += line
    }

    private fun createSegment(weight: Double) {
        val startPoint = if (lastElement == null) {
            Point(0.0, 0.0)
        } else {
            Point((lastElement as SegmentJoint).point.x, (lastElement as SegmentJoint).point.y)
        }
        val endPoint =
            Point(startPoint.x.plus(Random.nextDouble(10.0, 100.0)), startPoint.y.plus(Random.nextDouble(10.0, 100.0)))
        val segment = ChainSegment(null, weight, SystemCoordinate(1337.0), endPoint, startPoint)
        createSegmentButton.isDisable = true
        createJointButton.isDisable = !(currentMaxAngle != null && currentJointWeight != null)
        val chain = ChainControllerImpl.addChainElement(segment, lastElement).copy()
        drawChain(chain)
    }

    private fun createJoint(weight: Double, maxAngle: Double) {
        val point = if (lastElement == null) {
            Point(0.0, 0.0)
        } else {
            Point((lastElement as ChainSegment).endPoint.x, (lastElement as ChainSegment).endPoint.y)
        }
        val joint = SegmentJoint(null, weight, SystemCoordinate(228.0), point, maxAngle, null)
        createJointButton.isDisable = true
        createSegmentButton.isDisable = currentSegmentWeight == null
        val chain = ChainControllerImpl.addChainElement(joint, lastElement).copy()
        drawChain(chain)
    }

    private fun deleteElement() {
        val chain = ChainControllerImpl.deleteChainElement(lastElement!!).copy()
        lastElement = null
        drawChain(chain)
    }

    private fun chooseElement(evt: MouseEvent) {
        chainMap.values.firstOrNull {
            val mousePt: Point2D = it.sceneToLocal(evt.sceneX, evt.sceneY)
            it.contains(mousePt)
        }.apply {
            if (this != null) {
                if (this.hasClass(Styles.selected)) {
                    this.removeClass(Styles.selected)
                    selectedElement = null
                } else {
                    selectElement(this)
                }
            }
        }
    }

    private fun dragElement(evt: MouseEvent) {
        val mousePoint: Point2D = (evt.source as Pane).sceneToLocal(evt.sceneX, evt.sceneY)
        if (selectedShape != null && selectedElement != null) {
            when (selectedElement!!.elementType) {
                ChainElementType.SEGMENT -> {
                    val segment = selectedElement as ChainSegment
                    segment.endPoint.x = mousePoint.x
                    segment.endPoint.y = mousePoint.y
                    val chain = ChainControllerImpl.updateChainElement(segment.id!!, segment).copy()
                    drawChain(chain)
                }
                ChainElementType.JOINT -> {
                    val joint = selectedElement as SegmentJoint
                    joint.point.x = mousePoint.x
                    joint.point.y = mousePoint.y
                    val chain = ChainControllerImpl.updateChainElement(joint.id!!, joint).copy()
                    drawChain(chain)
                }
            }
        }
    }

    private fun selectElement(shape: Shape) {
        for (element in chainMap) {
            if (element.value == shape) {
                selectedElement = element.key
                selectedShape = element.value
                selectedShape!!.addClass(Styles.selected)
            }
        }
    }
}
