package scraper.logic

import com.fasterxml.jackson.annotation.JsonProperty
import scraper.config.CONFIG

data class Table(
    val status: Int,
    val data: Data
) {
    fun myFaction() = data.result.stats.player.player_faction.factions[CONFIG.playerId]!!
    fun myRank() = data.result.player.first { it.player_id == CONFIG.playerId }.gamerank.toInt()
    fun averageOppElo() = data.result.player.averageOppElo()
    fun myStartPosition() = data.result.stats.player.starting_order.myStartPosition()
}

data class Data(
    val result: Result,
    val options: Options
)

data class Options(
    @JsonProperty("105") val board: Option, // 4 = lakes
    @JsonProperty("201") val mode: Option // 2 = Arena
)

data class Option(
    val value: String
)

data class Result(
    val stats: Stats,
    val player: List<PlayerResult>
)

data class PlayerResult(
    val player_id: String,
    val finish_game: String, // 0 = false
    val gamerank: String, // Number
    private val rank_after_game: String, // elo + 1300
    private val point_win: String,

    ) {
    fun eloBeforeGame(): Double = rank_after_game.toDouble() - point_win.toDouble() - 1300
}

fun List<PlayerResult>.averageOppElo(): Double {
    var totalElo = 0.0
    this.forEach {
        if (it.player_id != CONFIG.playerId) {
            totalElo += it.eloBeforeGame()
        }
    }
    return totalElo / 4
}

data class Stats(
    val table: TableStat,
    val player: PlayerStat
)

// TODO not complete
data class TableStat(
    val player_number: Map<String, Any>
)

// TODO not complete
data class PlayerStat(
    val player_faction: PlayerFactions,
    val starting_order: StartingOrder
)

data class PlayerFactions(
    @JsonProperty("valuelabels") val factions: Map<String, String>
)

data class StartingOrder(
    val values: Map<String, String>
) {
    fun myStartPosition(): Int = values[CONFIG.playerId]!!.toInt()
}