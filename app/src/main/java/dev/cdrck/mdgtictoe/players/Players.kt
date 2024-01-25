package dev.cdrck.mdgtictoe.players

import dev.cdrck.mdgtictoe.datas.PoinType

data class Players(
    val id: Int,
    val win: Int,
    val name: String,
    var pointType: PoinType
) {
    companion object {
        val Player1 = Players(
            id = 0,
            win = 0,
            name = "Player 1",
            pointType = PoinType.X
        )

        val Player2 = Players(
            id = 1,
            win = 0,
            name = "Player 2",
            pointType = PoinType.O
        )

        val Computer = Players(
            id = 2,
            win = 0,
            name = "Computer",
            pointType = PoinType.O
        )
    }
}
