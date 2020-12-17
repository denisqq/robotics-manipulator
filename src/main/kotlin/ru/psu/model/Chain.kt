package ru.psu.model

data class Chain(
    var rootElement: ChainElement? = null,
    var chainState: ChainState = ChainState.NOT_INITIALIZED,
    var systemCoordinate: SystemCoordinate? = null
)