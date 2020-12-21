package ru.psu.controller.impl

import ru.psu.controller.FileController
import ru.psu.service.FileService
import ru.psu.service.impl.JsonFileService
import java.io.File

object FileControllerImpl : FileController {
    override fun exportChain(directory: File) {
        val chain = ChainControllerImpl.getChain()
        JsonFileService.exportChain(chain, directory)
    }

    override fun importChain(file: File) {
        JsonFileService.import(file)
    }
}