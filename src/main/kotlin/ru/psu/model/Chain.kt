package ru.psu.model

data class Chain(var rootChainSegment: ChainSegment? = null,
                 var chainState: ChainState = ChainState.NOT_INITIALIZED,
                 var systemCoordinate: SystemCoordinate? = null
) {

}