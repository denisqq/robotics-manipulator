package ru.psu.styles

import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*

class Styles : Stylesheet() {
    companion object {
        val heading by cssclass()
        val selected by cssclass()
        val intersection by cssclass()
        val centerMass by cssclass()
    }

    init {
        label and heading {
            padding = box(10.px)
            fontSize = 20.px
            fontWeight = FontWeight.BOLD
        }

        selected {
            fill = Color.BLUE
            stroke = Color.BLUE
        }

        intersection {
            fill = Color.RED
            stroke = Color.RED
        }

        centerMass {
            fill = Color.GREEN
        }
    }
}