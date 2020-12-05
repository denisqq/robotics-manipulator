package ru.psu.service

import ru.psu.model.Chain
import ru.psu.model.ChainElement
import ru.psu.model.ChainSegment
import ru.psu.model.Point

interface ChainService {
    fun getChain(): Chain

    fun calculateChainCenterMass() : Point

    fun createElement(chainElement: ChainElement, rootElement: ChainElement): Chain
}