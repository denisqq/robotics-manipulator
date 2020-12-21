package ru.psu.service.impl

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import ru.psu.model.Chain
import ru.psu.model.ChainElement
import ru.psu.model.ChainSegment
import ru.psu.service.FileService
import java.io.File
import java.io.FileWriter
import java.time.LocalDateTime

object JsonFileService : FileService {
    private val objectMapper = ObjectMapper()

    init {
        objectMapper.registerKotlinModule()
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT)
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    override fun exportChain(chain: Chain, directory: File) {
        if(!directory.isDirectory) {
            throw IllegalArgumentException("Path must be a directory")
        }

        val generatedName = "dumped-chain-${LocalDateTime.now()}.json"
        val dump = objectMapper.writeValueAsString(chain)

        val file = File(directory, generatedName)
        val writer = FileWriter(file)

        writer.use {
            it.write(dump)
        }
    }

    override fun import(file: File) {
        val json = file.bufferedReader(Charsets.UTF_8).readText()
        val chain = objectMapper.readValue(json, Chain::class.java)

        ChainServiceImpl.instance.updateChain(chain)
    }
}