package ru.psu.service

import ru.psu.model.ChainElement

interface ChainElementService<T : ChainElement, R : ChainElement> {
    fun delete(element: T)
    fun update(id: Long, element: T): T
    fun createElement(element: T, parentElement: R?): T
}