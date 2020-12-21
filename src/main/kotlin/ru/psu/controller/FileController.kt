package ru.psu.controller

import java.io.File

interface FileController {

    fun exportChain(directory: File);

    fun importChain(file: File)
}