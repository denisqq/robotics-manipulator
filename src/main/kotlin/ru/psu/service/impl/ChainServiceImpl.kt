package ru.psu.service.impl

import ru.psu.model.Chain
import ru.psu.model.ChainSegment
import ru.psu.model.ChainState
import ru.psu.service.ChainSegmentService
import ru.psu.service.ChainService

class ChainServiceImpl : ChainService {
    private val chain: Chain = Chain()
    private val chainSegmentService: ChainSegmentService = ChainSegmentServiceImpl();

    override fun getChain(): Chain {
        return chain
    }

    override fun calculateChainCenterMass() {
        chain.rootChainSegment?.let {
            val point = chainSegmentService.calculateChainSegmentCenterMas(it)
        }
//        TODO("Not yet implemented")
    }

    override fun addChainSegmentToChain(segment: ChainSegment): Chain {
        if (chain.chainState == ChainState.NOT_INITIALIZED) {
            chain.rootChainSegment = segment;
            chain.chainState = ChainState.INITIALIZED
        } else {
            val latestSegment = chainSegmentService.getLatestSegment()
            latestSegment?.let {
                latestSegment.children.add(segment)
            }
        }

        chainSegmentService.addToChainSegmentIndexes(segment)
        return chain;
    }

}