package ru.psu.view

import ru.psu.styles.Styles
import tornadofx.*

class MainView : View("MainView") {
    override val root = hbox {
        label(title) {
            addClass(Styles.heading)
        }
    }
}
