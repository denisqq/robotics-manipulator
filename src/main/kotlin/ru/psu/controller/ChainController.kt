package ru.psu.controller

import ru.psu.model.Chain
import ru.psu.model.ChainElement

interface ChainController {
    fun getChain(): Chain
    fun addChainElement(chainElement: ChainElement, rootElement: ChainElement): Chain
    fun deleteChainElement(chainElement: ChainElement): Chain
    fun updateChainElement(id: Long, chainElement: ChainElement): Chain
}