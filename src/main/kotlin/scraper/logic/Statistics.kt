package scraper.logic

class Statistics(private val tables: List<Table>) {

    private val factionStats = factions()
    private val positionStats = positionStats()

    private fun positionStats(): List<StartPositionStat> {
        val positions = mutableListOf<StartPositionStat>()

        tables.forEach { table ->
            val oppElo = table.averageOppElo()
            val rank = table.myRank()
            val myStartPos = table.myStartPosition()
            val currentValue = positions.firstOrNull { it.position == myStartPos }
            if (currentValue == null) {
                positions.add(
                    StartPositionStat(
                        ranks = mutableListOf(rank),
                        opponentElos = mutableListOf(oppElo),
                        position = myStartPos
                    )
                )
            } else {
                currentValue.ranks.add(rank)
                currentValue.opponentElos.add(table.averageOppElo())
            }
        }
        return positions
    }

    private fun factions(): List<FactionStat> {
        val factions = mutableListOf<FactionStat>()

        tables.forEach { table ->
            val faction = table.myFaction()
            val position = table.myRank()
            val currentValue = factions.firstOrNull { it.name == faction }
            if (currentValue == null) {
                factions.add(
                    FactionStat(
                        name = faction,
                        ranks = mutableListOf(position),
                        opponentElo = mutableListOf(table.averageOppElo())
                    )
                )
            } else {
                currentValue.count += 1
                currentValue.ranks.add(position)
                currentValue.opponentElo.add(table.averageOppElo())
            }
        }
        return factions
    }

    fun printStats() {
        val factionStats = factionStats.sortedBy { it.averagePosition() }
        val positionStats = positionStats.sortedBy { it.position }

        println(String.format("%-10s %-15s %-10s %-10s", "Position", "Average rank", "Average opp elo", "Count"))
        positionStats.forEach {
            println(
                String.format(
                    "%-10s %-15.2f %-15.2f %-10s",
                    it.position,
                    it.ranks.average(),
                    it.opponentElos.average(),
                    it.opponentElos.size
                )
            )
        }
        println()

        println(String.format("%-15s %-15s %-10s %-10s", "Faction", "Average pos", "Play count", "Average opp elo"))
        factionStats.forEach {
            println(
                String.format(
                    "%-15s %-15.2f %-10s %-10.2f",
                    it.name,
                    it.averagePosition(),
                    it.count,
                    it.averageOpponentElo()
                )
            )
        }
    }

}

data class FactionStat(
    val name: String,
    val ranks: MutableList<Int>,
    var count: Int = 1,
    val opponentElo: MutableList<Double>
) {
    fun averagePosition(): Double = ranks.average()
    fun averageOpponentElo(): Double = opponentElo.average()
}

data class StartPositionStat(
    val position: Int,
    val opponentElos: MutableList<Double>,
    val ranks: MutableList<Int>
)