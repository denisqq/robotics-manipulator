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
import ru.psu.controller.impl.IntersectControllerImpl
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
    private var centerMass = SimpleStringProperty("Для расчета центра масс всей цепи нажмите на эту кнопку")

    companion object {
        private var chainMap: MutableMap<ChainElement, Shape> = HashMap()

        private var selectedElement: ChainElement? = null
        private var selectedShape: Shape? = null
        private var selectedOffset: Point2D? = null
        private var currentElement: ChainElement? = null
        private var centerMassPoint: Point? = null

        private var currentSegmentIsEphemeral = false
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
        setPrefSize(Double.MAX_VALUE, 1024.0)
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
                spacing = 10.0
                fieldset("Сегмент") {
                    field("Вес:") {
                        textfield {
                            textProperty().addListener { _, _, newValue ->
                                currentSegmentWeight = newValue.toDoubleOrNull()
                                createSegmentButton.isDisable = currentSegmentWeight == null
                                updateSegmentButton.isDisable = currentSegmentWeight == null
                            }
                            this.text = (currentElement as ChainSegment?)?.weight.toString().let { "" }
                        }
                    }
                    hbox(spacing = 50.0, alignment = Pos.CENTER) {
                        checkbox("Невидимый сегмент?") {
                            this.isSelected = currentSegmentIsHidden
                            action { changeVisibility() }
                        }
                        checkbox("Эфемерный сегмент?") {
                            this.isSelected = currentSegmentIsEphemeral
                            action { changeEphemerality() }
                        }
                        paddingAll = 10.0
                    }
                    hbox(spacing = 50.0, alignment = Pos.CENTER) {
                        this.addChildIfPossible(createSegmentButton)
                        this.addChildIfPossible(updateSegmentButton)
                        paddingAll = 10.0
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
                            this.text = (currentElement as SegmentJoint?)?.weight.toString().let { "" }
                        }
                    }
                    field("Максимальный угол:") {
                        textfield {
                            textProperty().addListener { _, _, newValue ->
                                currentMaxAngle = newValue.toDoubleOrNull()
                                createJointButton.isDisable = !(currentMaxAngle != null && currentJointWeight != null)
                                updateJointButton.isDisable = !(currentMaxAngle != null && currentJointWeight != null)
                            }
                            this.text = (currentElement as SegmentJoint?)?.maxAngle.toString().let { "" }
                        }
                    }
                    hbox(spacing = 50.0, alignment = Pos.CENTER) {
                        this.addChildIfPossible(createJointButton)
                        this.addChildIfPossible(updateJointButton)
                        paddingAll = 10.0
                    }
                }
                fieldset("Центр масс") {
                    spacing = 10.0
                    label(centerMass)
                    hbox(spacing = 50.0, alignment = Pos.CENTER) {
                        button("Рассчитать центр масс цепи") {
                            action {
                                centerMassPoint = ChainControllerImpl.calculateCenterMass()
                                centerMassPoint?.let { drawCenterMass(it) }
                                centerMass.value =
                                    "X: ${centerMassPoint?.x}, Y: ${centerMassPoint?.y}"
                            }
                        }
                    }
                }
                fieldset("Импорт/Экспорт") {
                    hbox(spacing = 50.0, alignment = Pos.CENTER) {
                        paddingAll = 10.0
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
            }
            paddingAll = 10.0
        }
        bottom {
            hbox(10.0, Pos.BASELINE_LEFT) {
                button("Удалить выбраный элемент") {
                    action { deleteElement() }
                }
                label(elementInfo) { }
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
        for (element in chainMap) {
            if (element.key == currentElement) {
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
        if (currentElement == null || currentElement == chainSegment.parentSegmentJoint) {
            removeBlueColor()
            if (chainSegment.parentSegmentJoint != null) {
                var prevId: Long = -1
                for (segment in chainSegment.parentSegmentJoint!!.childSegments) {
                    val id = segment.id!!
                    if (id > prevId) {
                        currentElement = segment
                        addElementInfo(currentElement as ChainSegment)
                    }
                    prevId = id
                }
            } else {
                currentElement = chainSegment
                addElementInfo(currentElement as ChainSegment)
            }
        }
        chainMap[chainSegment] = line
        workArea += line
    }

    private fun drawJoint(joint: SegmentJoint) {
        val centerPoint = joint.point
        val circle = Circle(centerPoint.x, centerPoint.y, 10.0)
        if (currentElement == null || currentElement == joint.parentSegment) {
            removeBlueColor()
            currentElement = joint
            addElementInfo(currentElement as SegmentJoint)
        }
        chainMap[joint] = circle
        workArea += circle
    }

    private fun drawCenterMass(point: Point) {
        workArea.removeClass(Styles.centerMass)
        val circle = Circle(point.x, point.y, 15.0)
        circle.addClass(Styles.centerMass)
        workArea += circle
    }

    private fun createSegment(): ChainSegment {
        var startPoint = Point(0.0, 0.0)
        var endPoint = Point(0.0, 0.0)
        when (currentElement?.elementType) {
            ChainElementType.SEGMENT -> {
                startPoint = (currentElement as ChainSegment).startPoint
                endPoint = (currentElement as ChainSegment).endPoint
            }
            ChainElementType.JOINT -> {
                startPoint = (currentElement as SegmentJoint).point
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
            hidden = currentSegmentIsHidden,
            ephemeral = currentSegmentIsEphemeral
        )
        createJointButton.isDisable = !(currentMaxAngle != null && currentJointWeight != null)
        updateJointButton.isDisable = !(currentMaxAngle != null && currentJointWeight != null)
        return segment
    }

    private fun createJoint(): SegmentJoint {
        val point = when (currentElement?.elementType) {
            ChainElementType.SEGMENT -> {
                (currentElement as ChainSegment).endPoint
            }
            ChainElementType.JOINT -> {
                (currentElement as SegmentJoint).point
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
        val chain = ChainControllerImpl.addChainElement(chainElement, currentElement).copy()
        drawChain(chain)
    }

    private fun updateChainElement(chainElement: ChainElement) {
        addElementInfo(chainElement)
        val chain = ChainControllerImpl.updateChainElement(currentElement!!.id!!, chainElement).copy()
        drawChain(chain)
    }

    private fun deleteElement() {
        val chain = ChainControllerImpl.deleteChainElement(currentElement!!).copy()
        currentElement = null
        drawChain(chain)
    }

    private fun startElementDragging(evt: MouseEvent) {
        chainMap.values.firstOrNull {
            val mousePt: Point2D = it.sceneToLocal(evt.sceneX, evt.sceneY)
            it.contains(mousePt)
        }.apply {
            if (this != null) {
                selectElement(this)
                val mp = this.parent.sceneToLocal(evt.sceneX, evt.sceneY)
                val vizBounds = this.boundsInParent

                selectedOffset = Point2D(
                    mp.x - vizBounds.minX - (vizBounds.width - this.boundsInLocal.width) / 2,
                    mp.y - vizBounds.minY - (vizBounds.height - this.boundsInLocal.height) / 2
                )
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
                    addElementInfo(segment)
                    val chain = ChainControllerImpl.updateChainElement(segment.id!!, segment).copy()
                    drawChain(chain)
                }
                ChainElementType.JOINT -> {
                    val joint = selectedElement as SegmentJoint
                    joint.point.x = mousePoint.x
                    joint.point.y = mousePoint.y
                    addElementInfo(joint)
                    val chain = ChainControllerImpl.updateChainElement(joint.id!!, joint).copy()
                    drawChain(chain)
                }
            }
            checkIntersect(Point(mousePoint.x, mousePoint.y))
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
                    currentElement = element.key
                    addElementInfo(currentElement!!)
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

    private fun changeEphemerality() {
        currentSegmentIsEphemeral = !currentSegmentIsEphemeral
    }

    private fun changeButtonStatus() {
        if (currentElement?.elementType == ChainElementType.SEGMENT) {
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

    private fun checkIntersect(mousePoint: Point) {
        if (mousePoint.x - selectedOffset!!.x == 10.0 || mousePoint.y - selectedOffset!!.y == 10.0) {
            val intersectionElement = IntersectControllerImpl.findIntersect(mousePoint)
            for (element in intersectionElement) {
                chainMap[element]?.addClass(Styles.intersection)
            }
        }
    }
}
