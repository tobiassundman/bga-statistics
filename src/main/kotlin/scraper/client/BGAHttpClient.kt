package scraper.client

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import scraper.config.CONFIG
import java.io.IOException

class BGAHttpClient {
    private val getGamesUrl = "https://en.boardgamearena.com/gamestats/gamestats/getGames.html?player=${CONFIG.playerId}&updateStats=0"
    private val getTableUrl = "https://boardgamearena.com/table/table/tableinfos.html"

    private val client = OkHttpClient.Builder().build()

    fun getTableIds(page: Int): List<String> {
        Thread.sleep(CONFIG.callBackoffMs)
        val request = Request.Builder()
            .url("$getGamesUrl&page=$page")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            val body = JSONObject(response.body!!.string())
            
            val tables = body.getJSONObject("data").getJSONArray("tables")

            val tableIds = mutableListOf<String>()
            for (index in 0 until tables.length()) {
                val tableId = tables.getJSONObject(index).getString("table_id")
                tableIds.add(tableId)
            }
            return tableIds
        }
    }

    fun getGame(tableId: String): String {
        Thread.sleep(CONFIG.callBackoffMs)
        val request = Request.Builder()
            .url("$getTableUrl?id=$tableId")
            .addHeader("Cookie", CONFIG.cookie)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            return response.body!!.string().also {
                println("Got table: $it")
                if (it.contains("masked")) throw IllegalStateException("Masked - update cookie")
            }
        }
    }
}