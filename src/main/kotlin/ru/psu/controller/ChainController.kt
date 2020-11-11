package ru.psu.controller

import ru.psu.model.Chain

interface ChainController {
    fun getChain(): Chain

    fun addChainSegment()
}