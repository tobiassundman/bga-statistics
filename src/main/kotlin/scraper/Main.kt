package scraper

import scraper.client.BGAHttpClient
import scraper.config.CONFIG
import scraper.logic.Filter
import scraper.logic.Statistics

fun main(args: Array<String>) {
    Main().execute()
}

class Main {
    private val fileManager = FileManager()
    private val client = BGAHttpClient()

    fun execute() {
        if (CONFIG.fetchNewest) {
            println("Fetching newest info from web")
            fetchNewestInfo()
        }
        val allIds = fileManager.readTableIds()

        val tables = fileManager.readTables(allIds)

        Statistics(Filter(tables).lakeArenaGames()).printStats()
    }

    private fun fetchNewestInfo() {
        val knownIds = fileManager.readTableIds()
        println("Known ids: $knownIds")
        writeUnknownTableIds(knownIds)

        val allIds = fileManager.readTableIds()
        allIds.filter { !fileManager.tableFileExists(it) }
            .forEach {
                val tableJson = client.getGame(it)
                fileManager.writeTable(it, tableJson)
            }
    }

    private fun writeUnknownTableIds(knownIds: List<String>) {
        var index = 1
        while (true) {
            val tableIds = client.getTableIds(index)
            println("Got table ids $tableIds")

            if (tableIds.isEmpty() || tableIds.all { it in knownIds }) {
                break
            }

            tableIds.forEach {
                if (it !in knownIds) {
                    fileManager.appendTableId(it)
                }
            }
            index++
        }
    }
}
