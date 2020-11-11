package ru.psu.service

import ru.psu.model.Chain
import ru.psu.model.ChainSegment

interface ChainService {
    fun getChain(): Chain

    fun calculateChainCenterMass();

    fun addChainSegmentToChain(segment: ChainSegment): Chain
}