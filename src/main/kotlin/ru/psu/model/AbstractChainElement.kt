package ru.psu.model


abstract class AbstractChainElement : ChainElement {
    abstract var weight: Double
    override var deleted: Boolean = false
}