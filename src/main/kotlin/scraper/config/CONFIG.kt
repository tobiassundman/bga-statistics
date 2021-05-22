package scraper.config

import org.json.JSONObject
import java.io.File
import java.io.FileReader

object CONFIG {
    const val basePath = "C:\\Users\\zarito\\IdeaProjects\\bga-scraper"

    val configFile = "$basePath\\src\\main\\resources\\config.json"
    val jsonConfig = readConfig()

    val callBackoffMs = 10_000L
    val playerId = Players.ZARITO.id

    val fetchNewest = jsonConfig.getBoolean("fetchNewest")
    val cookie = jsonConfig.getString("cookie")

    val tableIdsFile = "$basePath\\src\\main\\resources\\${playerId}\\tableIds"
    val tablesDirectory = "$basePath\\src\\main\\resources\\${playerId}\\tables"

    private fun readConfig(): JSONObject {
        require(File(configFile).exists())
        FileReader(configFile).use {
            return JSONObject(it.readText())
        }
    }

}

enum class Players(val id: String) {
    KUTUDAKI("90310730"),
    ZARITO("90270058")
}