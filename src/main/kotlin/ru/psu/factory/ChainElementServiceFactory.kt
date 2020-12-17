package ru.psu.factory

import ru.psu.model.ChainElement
import ru.psu.service.ChainElementService

interface ChainElementServiceFactory<E : ChainElement, out S : ChainElementService<E, E>> {

    fun create(element: ChainElement): S
}