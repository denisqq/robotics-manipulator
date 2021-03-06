package ru.psu.controller

import ru.psu.model.Chain
import ru.psu.model.ChainElement
import ru.psu.model.Point

interface ChainController {
    fun getChain(): Chain
    fun addChainElement(chainElement: ChainElement, rootElement: ChainElement?): Chain
    fun deleteChainElement(chainElement: ChainElement): Chain
    fun updateChainElement(id: Long, chainElement: ChainElement): Chain
    fun calculateCenterMass(): Point
}