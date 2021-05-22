package scraper.logic

class Filter(tables: List<Table>) {
    // Filter out games that didn't finish
    val tables = tables.filter { table -> table.data.result.player.all { it.finish_game == "1" } }

    fun lakeArenaGames(): List<Table> {
        return tables.filter {
            it.data.options.board.value == "4" && it.data.options.mode.value == "2"
        }
    }

}