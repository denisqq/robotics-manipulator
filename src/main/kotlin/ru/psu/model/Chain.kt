package ru.psu.model

data class Chain(var rootChainSegment: ChainSegment? = null, var chainState: ChainState = ChainState.NOT_INITIALIZED) {

}