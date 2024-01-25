package dev.cdrck.mdgtictoe.util

import dev.cdrck.mdgtictoe.datas.PoinType
import dev.cdrck.mdgtictoe.datas.TurnType
import dev.cdrck.mdgtictoe.datas.WinType
import dev.cdrck.mdgtictoe.players.Players
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameUtil(
    private var playerOne: Players,
    private var playerTwo: Players,
) {

    private val defaultBoard = ArrayList<PoinType>(9).apply {
        repeat(9) { i ->
            add(i, PoinType.Empty)
        }
    }

    private val _board = MutableStateFlow(defaultBoard)
    val board: StateFlow<ArrayList<PoinType>> = _board.asStateFlow()

    private val _currentTurn = MutableStateFlow(TurnType.PlayerOne)
    val currentTurn: StateFlow<TurnType> = _currentTurn.asStateFlow()

    private var listener: Listener? = null

    private fun checkHorizontal(board: List<PoinType>): WinType {
        for (i in 0 until 3) {
            when {
                board[i * 3] == PoinType.O && board[(i * 3) + 1] == PoinType.O && board[(i * 3) + 2] == PoinType.O -> {
                    return WinType.O
                }
                board[i * 3] == PoinType.X && board[(i * 3) + 1] == PoinType.X && board[(i * 3) + 2] == PoinType.X -> {
                    return WinType.X
                }
            }
        }

        return WinType.None
    }

    private fun checkVertical(board: List<PoinType>): WinType {
        for (i in 0 until 3) {
            when {
                board[i] == PoinType.O && board[i + 3] == PoinType.O && board[i + 6] == PoinType.O -> {
                    return WinType.O
                }
                board[i] == PoinType.X && board[i + 3] == PoinType.X && board[i + 6] == PoinType.X -> {
                    return WinType.X
                }
            }
        }

        return WinType.None
    }

    private fun checkDiagonal(board: List<PoinType>): WinType {
        when {
            board[0] == PoinType.O && board[4] == PoinType.O && board[8] == PoinType.O -> {
                return WinType.O
            }
            board[0] == PoinType.X && board[4] == PoinType.X && board[8] == PoinType.X -> {
                return WinType.X
            }
            board[2] == PoinType.O && board[4] == PoinType.O && board[6] == PoinType.O -> {
                return WinType.O
            }
            board[2] == PoinType.X && board[4] == PoinType.X && board[6] == PoinType.X -> {
                return WinType.X
            }
        }

        return WinType.None
    }

    private fun checkWin(board: List<PoinType>): WinType {
        val wins = listOf(checkHorizontal(board), checkVertical(board), checkDiagonal(board))
        val isTie = wins.all { it == WinType.None } and board.all { it != PoinType.Empty }

        val winner = when {
            isTie -> WinType.Tie
            WinType.O in wins -> WinType.O
            WinType.X in wins -> WinType.X
            else -> WinType.None
        }

        listener?.onWin(winner)

        return winner
    }

    suspend fun updateBoard(index: Int) {
        val newPoinType = if (currentTurn.value == TurnType.PlayerOne) playerOne.pointType else playerTwo.pointType
        val newBoard = ArrayList(board.value).apply {
            set(index, newPoinType)
        }

        _board.emit(newBoard)

        val winner = checkWin(newBoard)

        val nextTurn = if (currentTurn.value == TurnType.PlayerOne) TurnType.PlayerTwo else TurnType.PlayerOne

        _currentTurn.emit(nextTurn)

        if (winner == WinType.None) {
            when (nextTurn) {
                TurnType.PlayerOne -> {
                    if (playerOne.id == Players.Computer.id) {
                        computerTurn(newBoard)
                    }
                }
                TurnType.PlayerTwo -> {
                    if (playerTwo.id == Players.Computer.id) {
                        computerTurn(newBoard)
                    }
                }
            }
        }
    }

    private suspend fun computerTurn(board: List<PoinType>) {
        val emptyIndex = arrayListOf<Int>()

        board.forEachIndexed { i, type ->
            if (type == PoinType.Empty) emptyIndex.add(i)
        }

        if (emptyIndex.isNotEmpty()) {
            val randomIndex = emptyIndex.random()

            updateBoard(randomIndex)
        }
    }

    suspend fun clearBoard() {
        _board.emit(defaultBoard)

        when (currentTurn.value) {
            TurnType.PlayerOne -> {
                if (playerOne.id == Players.Computer.id) {
                    computerTurn(defaultBoard)
                }
            }
            TurnType.PlayerTwo -> {
                if (playerTwo.id == Players.Computer.id) {
                    computerTurn(defaultBoard)
                }
            }
        }
    }

    fun updatePlayer(one: Players, two: Players) {
        playerOne = one
        playerTwo = two
    }

    fun setListener(l: Listener) {
        listener = l
    }

    interface Listener {

        fun onWin(type: WinType)
    }

}