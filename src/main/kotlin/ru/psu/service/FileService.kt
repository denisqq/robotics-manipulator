package ru.psu.service

import ru.psu.model.Chain
import java.io.File

interface FileService {
    fun exportChain(chain: Chain, directory: File)

    fun import(file: File)
}