package ru.psu.view

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Point2D
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.shape.Circle
import javafx.scene.shape.Line
import javafx.scene.shape.Shape
import javafx.stage.FileChooser
import ru.psu.controller.impl.ChainControllerImpl
import ru.psu.controller.impl.FileControllerImpl
import ru.psu.model.*
import ru.psu.model.enums.ChainElementType
import ru.psu.styles.Styles
import tornadofx.*
import kotlin.random.Random

class MainView : View("MainView") {
    private var workArea: Pane by singleAssign()
    private var createSegmentButton = Button("Создать сегмент")
    private var createJointButton = Button("Создать сустав")
    private var updateSegmentButton = Button("Изменить сегмент")
    private var updateJointButton = Button("Изменить сустав")
    private var elementInfo = SimpleStringProperty("Тут будет показана основная информация об объекте")

    companion object {
        private var chainMap: MutableMap<ChainElement, Shape> = HashMap()

        private var selectedElement: ChainElement? = null
        private var selectedShape: Shape? = null
        private var lastElement: ChainElement? = null

        private var currentSegmentIsHidden = false
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
        createSegmentButton.action { addChainElement(createSegment()) }
        createJointButton.isDisable = true
        createJointButton.action { addChainElement(createJoint()) }

        updateSegmentButton.isDisable = true
        updateSegmentButton.action { updateChainElement(createSegment()) }
        updateJointButton.isDisable = true
        updateJointButton.action { updateChainElement(createJoint()) }
    }

    @Suppress("DuplicatedCode")
    override val root = borderpane {
        setPrefSize(1280.0, 1024.0)
        left {
            workArea = pane {
                addEventFilter(MouseEvent.MOUSE_CLICKED, ::chooseElement)
                addEventFilter(MouseEvent.MOUSE_PRESSED, ::startElementDragging)
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
                                createSegmentButton.isDisable = currentSegmentWeight == null
                                updateSegmentButton.isDisable = currentSegmentWeight == null
                            }
                            this.text = (lastElement as ChainSegment?)?.weight.toString().let { "" }
                        }
                    }
                    checkbox("Невидимый сегмент?") {
                        this.isSelected = currentSegmentIsHidden
                        action { changeVisibility() }
                    }
                    hbox(spacing = 50.0, alignment = Pos.CENTER) {
                        this.addChildIfPossible(createSegmentButton)
                        this.addChildIfPossible(updateSegmentButton)
                    }
                }
                fieldset("Сустав") {
                    field("Вес:") {
                        textfield {
                            textProperty().addListener { _, _, newValue ->
                                currentJointWeight = newValue.toDoubleOrNull()
                                createJointButton.isDisable = !(currentMaxAngle != null && currentJointWeight != null)
                                updateJointButton.isDisable = !(currentMaxAngle != null && currentJointWeight != null)
                            }
                            this.text = (lastElement as SegmentJoint?)?.weight.toString().let { "" }
                        }
                    }
                    field("Максимальный угол:") {
                        textfield {
                            textProperty().addListener { _, _, newValue ->
                                currentMaxAngle = newValue.toDoubleOrNull()
                                createJointButton.isDisable = !(currentMaxAngle != null && currentJointWeight != null)
                                updateJointButton.isDisable = !(currentMaxAngle != null && currentJointWeight != null)
                            }
                            this.text = (lastElement as SegmentJoint?)?.maxAngle.toString().let { "" }
                        }
                    }
                    hbox(spacing = 50.0, alignment = Pos.CENTER) {
                        this.addChildIfPossible(createJointButton)
                        this.addChildIfPossible(updateJointButton)
                    }
                }
                button("Удалить") {
                    action { deleteElement() }
                }
                button("Экспортировать") {
                    action {
                        val dir = chooseDirectory("Выберите директорию")
                        FileControllerImpl.exportChain(dir!!)
                    }
                }

                button("Импортировать") {
                    action {
                        val files = chooseFile(
                            title = "Выберите дамп",
                            filters = arrayOf(FileChooser.ExtensionFilter("JSON", "*.json")),
                            null
                        )
                        val file = files[0]
                        FileControllerImpl.importChain(file)
                    }
                }
            }
        }
        bottom {
            label(elementInfo) { paddingAll = 2.0 }
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
        for (element in chainMap) {
            if (element.key == lastElement) {
                element.value.addClass(Styles.selected)
                changeButtonStatus()
            }
        }
    }

    private fun drawSegment(chainSegment: ChainSegment) {
        if (chainSegment.hidden) {
            return
        }
        val startPoint = chainSegment.startPoint
        val endPoint = chainSegment.endPoint
        val line = Line(startPoint.x, startPoint.y, endPoint.x, endPoint.y)
        line.strokeWidth = 5.0
        if (lastElement == null || lastElement == chainSegment.parentSegmentJoint) {
            removeBlueColor()
            if (chainSegment.parentSegmentJoint != null) {
                var prevId: Long = -1
                for (segment in chainSegment.parentSegmentJoint!!.childSegments) {
                    val id = segment.id!!
                    if (id > prevId) {
                        lastElement = segment
                        addElementInfo(lastElement as ChainSegment)
                    }
                    prevId = id
                }
            } else {
                lastElement = chainSegment
                addElementInfo(lastElement as ChainSegment)
            }
        }
        chainMap[chainSegment] = line
        workArea += line
    }

    private fun drawJoint(joint: SegmentJoint) {
        val centerPoint = joint.point
        val circle = Circle(centerPoint.x, centerPoint.y, 10.0)
        if (lastElement == null || lastElement == joint.parentSegment) {
            removeBlueColor()
            lastElement = joint
            addElementInfo(lastElement as SegmentJoint)
        }
        chainMap[joint] = circle
        workArea += circle
    }

    private fun createSegment(): ChainSegment {
        var startPoint = Point(0.0, 0.0)
        var endPoint = Point(0.0, 0.0)
        when (lastElement?.elementType) {
            ChainElementType.SEGMENT -> {
                startPoint = (lastElement as ChainSegment).startPoint
                endPoint = (lastElement as ChainSegment).endPoint
            }
            ChainElementType.JOINT -> {
                startPoint = (lastElement as SegmentJoint).point
                endPoint = Point(
                    startPoint.x.plus(Random.nextDouble(10.0, 100.0)),
                    startPoint.y.plus(Random.nextDouble(10.0, 100.0))
                )
            }
            null -> {
                startPoint = Point(10.0, 10.0)
                endPoint = Point(50.0, 50.0)
            }
        }
        val segment = ChainSegment(
            null,
            currentSegmentWeight!!,
            SystemCoordinate(1337.0),
            endPoint,
            startPoint,
            hidden = currentSegmentIsHidden
        )
        createJointButton.isDisable = !(currentMaxAngle != null && currentJointWeight != null)
        updateJointButton.isDisable = !(currentMaxAngle != null && currentJointWeight != null)
        return segment
    }

    private fun createJoint(): SegmentJoint {
        val point = when (lastElement?.elementType) {
            ChainElementType.SEGMENT -> {
                (lastElement as ChainSegment).endPoint
            }
            ChainElementType.JOINT -> {
                (lastElement as SegmentJoint).point
            }
            null -> {
                Point(10.0, 10.0)
            }
        }
        val joint = SegmentJoint(null, currentJointWeight!!, SystemCoordinate(228.0), point, currentMaxAngle!!, null)
        createSegmentButton.isDisable = currentSegmentWeight == null
        updateSegmentButton.isDisable = currentSegmentWeight == null
        return joint
    }

    private fun addChainElement(chainElement: ChainElement) {
        val chain = ChainControllerImpl.addChainElement(chainElement, lastElement).copy()
        drawChain(chain)
    }

    private fun updateChainElement(chainElement: ChainElement) {
        val chain = ChainControllerImpl.updateChainElement(lastElement!!.id!!, chainElement).copy()
        drawChain(chain)
    }

    private fun deleteElement() {
        val chain = ChainControllerImpl.deleteChainElement(lastElement!!).copy()
        lastElement = null
        drawChain(chain)
    }

    private fun startElementDragging(evt: MouseEvent) {
        chainMap.values.firstOrNull {
            val mousePt: Point2D = it.sceneToLocal(evt.sceneX, evt.sceneY)
            it.contains(mousePt)
        }.apply {
            if (this != null) {
                selectElement(this)
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

    private fun endDrag(@Suppress("UNUSED_PARAMETER") evt: MouseEvent) {
        selectedShape = null
        selectedElement = null
    }

    private fun chooseElement(evt: MouseEvent) {
        if (evt.clickCount == 2) {
            for (element in chainMap) {
                val mousePt: Point2D = element.value.sceneToLocal(evt.sceneX, evt.sceneY)
                if (element.value.contains(mousePt)) {
                    removeBlueColor()
                    element.value.addClass(Styles.selected)
                    lastElement = element.key
                    addElementInfo(lastElement!!)
                }
                changeButtonStatus()
            }
        }
    }

    private fun selectElement(shape: Shape) {
        for (element in chainMap) {
            if (element.value == shape) {
                selectedElement = element.key
                selectedShape = element.value
            }
        }
    }

    private fun removeBlueColor() {
        chainMap.values.forEach {
            if (it.hasClass(Styles.selected)) {
                it.removeClass(Styles.selected)
            }
        }
    }

    private fun changeVisibility() {
        currentSegmentIsHidden = !currentSegmentIsHidden
    }

    private fun changeButtonStatus() {
        if (lastElement?.elementType == ChainElementType.SEGMENT) {
            createSegmentButton.isDisable = true
            updateSegmentButton.isDisable = false
            if (currentMaxAngle != null && currentJointWeight != null) {
                createJointButton.isDisable = false
                updateJointButton.isDisable = true
            }
        } else {
            createJointButton.isDisable = true
            updateJointButton.isDisable = false
            if (currentSegmentWeight != null) {
                createSegmentButton.isDisable = false
                updateSegmentButton.isDisable = true
            }
        }
    }

    private fun addElementInfo(chainElement: ChainElement) {
        when (chainElement.elementType) {
            ChainElementType.SEGMENT -> {
                chainElement as ChainSegment

                elementInfo.value =
                    "Начало: (${chainElement.startPoint.x}, ${chainElement.startPoint.y}), Конец: (${chainElement.endPoint.x}, ${chainElement.endPoint.y}), Вес: ${chainElement.weight}, Эфимерный: ${
                        createYesString(chainElement.ephemeral)
                    }, Скрытый: ${createYesString(chainElement.hidden)}"
            }
            ChainElementType.JOINT -> {
                chainElement as SegmentJoint

                elementInfo.value =
                    "Центр: (${chainElement.point.x}, ${chainElement.point.y}), Вес: ${chainElement.weight}, Максимальный угол: ${chainElement.maxAngle}"
            }
        }
    }

    private fun createYesString(boolean: Boolean): String {
        return if (boolean) {
            "Да"
        } else {
            "Нет"
        }
    }
}
