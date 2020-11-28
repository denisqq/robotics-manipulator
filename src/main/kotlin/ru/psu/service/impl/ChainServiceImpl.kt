package ru.psu.service.impl

import ru.psu.model.Chain
import ru.psu.model.ChainSegment
import ru.psu.model.ChainState
import ru.psu.model.Point
import ru.psu.service.ChainSegmentService
import ru.psu.service.ChainService
import java.lang.IllegalArgumentException

class ChainServiceImpl : ChainService {
    private val chain: Chain = Chain()
    private val chainSegmentService: ChainSegmentService = ChainSegmentServiceImpl();

    override fun getChain(): Chain {
        return chain
    }

    override fun calculateChainCenterMass(): Point {
        chain.rootChainSegment?.let {
            return it.startPoint;
        }

        throw IllegalArgumentException("root segment not init yet, system does`t have system coordinates")
    }

    override fun addChainSegmentToChain(segment: ChainSegment): Chain {
        //TODO может быть проблема с конкуретным доступом к chain
        if (chain.chainState == ChainState.NOT_INITIALIZED) {
            val createdSegment = chainSegmentService.addChainSegment(segment)
            chain.chainState = ChainState.INITIALIZED
            chain.systemCoordinate = createdSegment.systemCoordinate
        } else {
            chainSegmentService.addChainSegment(segment)
        }
        return chain;
    }

}