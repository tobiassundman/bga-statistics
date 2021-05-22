package scraper

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.json.JSONObject
import scraper.config.CONFIG
import scraper.logic.Table
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class FileManager {
    private val objectMapper = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .registerModule(KotlinModule())

    fun readTableIds(): List<String> = FileReader(CONFIG.tableIdsFile).use { reader ->
        return reader.readLines().filter { it.isNotEmpty() }
    }

    fun appendTableId(tableId: String) {
        println("Appending $tableId to tableIds file")
        FileWriter(CONFIG.tableIdsFile, true).use { writer ->
            writer.write("$tableId\n")
        }
    }

    fun writeTable(tableId: String, tableJson: String) {
        val fileName = "${CONFIG.tablesDirectory}/$tableId"
        println("Writing $fileName")
        FileWriter(fileName).use { writer ->
            writer.write("$tableJson\n")
        }
    }

    fun readTables(tableIds: List<String>): List<Table> {
        val tables = mutableListOf<Table>()
        tableIds.forEach { id ->
            readTable(id)?.let {
                tables.add(it)
            }
        }
        return tables
    }

    private fun readTable(tableId: String): Table? {
        FileReader("${CONFIG.tablesDirectory}/$tableId").use {
            val text = it.readText()
            return if (isValidGame(text)) {
                try {
                    objectMapper.readValue(text, Table::class.java)
                } catch (e: Exception) {
                    println("Failed to deserialize $tableId $text")
                    throw e
                }
            } else {
                null
            }
        }
    }

    private fun isValidGame(table: String): Boolean {
        val json = JSONObject(table)
        json.getJSONObject("data")
            .getJSONObject("result")
            .getJSONArray("player")
            .forEach {
                if ((it as JSONObject).getString("finish_game") == "0") {
                    return false
                }
            }
        return true
    }

    fun tableFileExists(tableId: String): Boolean {
        val fileName = "${CONFIG.tablesDirectory}/$tableId"
        return File(fileName).exists()
    }
}