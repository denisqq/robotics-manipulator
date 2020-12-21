package ru.psu.service

import ru.psu.model.Chain
import ru.psu.model.ChainElement
import ru.psu.model.Point

interface ChainService {
    fun getChain(): Chain

    fun calculateChainCenterMass(): Point

    fun deleteElement(chainElement: ChainElement): Chain

    fun updateElement(id: Long, chainElement: ChainElement): Chain

    fun addElement(chainElement: ChainElement, rootElement: ChainElement?): Chain

    fun findElement(vararg point: Point): Collection<ChainElement>
}
