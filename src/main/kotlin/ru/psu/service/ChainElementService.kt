package ru.psu.service

import ru.psu.model.ChainElement

interface ChainElementService<T: ChainElement, R: ChainElement> {
    fun createElement(element: T, rootElement: R?): T
}