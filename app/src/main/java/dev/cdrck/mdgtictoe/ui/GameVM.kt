package dev.cdrck.mdgtictoe.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cdrck.mdgtictoe.datas.*
import dev.cdrck.mdgtictoe.util.GameUtil
import dev.cdrck.mdgtictoe.exts.to2DArray
import dev.cdrck.mdgtictoe.players.Players
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameVM @Inject constructor(
    savedStateHandle: SavedStateHandle
): ViewModel() {

    val board = mutableStateListOf<List<PoinType>>()

    var currentTurn by mutableStateOf(TurnType.PlayerOne)
        private set

    var playerOne by mutableStateOf(Players.Player1)
        private set

    var playerTwo by mutableStateOf(Players.Player2)
        private set

    var gameMode by mutableStateOf(GameMod.Computer)
        private set

    var winner by mutableStateOf(WinType.None)
        private set

    var round by mutableStateOf(1)
        private set

    var draw by mutableStateOf(0)
        private set

    private val gameUtil = GameUtil(
        playerOne = playerOne,
        playerTwo = playerTwo
    )

    private val _gameMode = savedStateHandle.getStateFlow(ARG_GAME_MODE, 0)

    init {
        gameUtil.setListener(object : GameUtil.Listener {
            override fun onWin(type: WinType) {
                winner = type

                when {
                    playerOne.pointType == PoinType.O && winner == WinType.O -> {
                        playerOne = playerOne.copy(win = playerOne.win + 1)
                    }
                    playerOne.pointType == PoinType.X && winner == WinType.X -> {
                        playerOne = playerOne.copy(win = playerOne.win + 1)
                    }
                    playerTwo.pointType == PoinType.O && winner == WinType.O -> {
                        playerTwo = playerTwo.copy(win = playerTwo.win + 1)
                    }
                    playerTwo.pointType == PoinType.X && winner == WinType.X -> {
                        playerTwo = playerTwo.copy(win = playerTwo.win + 1)
                    }
                    winner == WinType.Tie -> {
                        draw += 1
                    }
                }

                if (winner != WinType.None) {
                    round++
                }
            }
        })

        viewModelScope.launch {
            gameUtil.board.collect { mBoard ->
                board.apply {
                    clear()
                    addAll(mBoard.to2DArray(3))
                }
            }
        }

        viewModelScope.launch {
            gameUtil.currentTurn.collect { turn ->
                currentTurn = turn
            }
        }

        viewModelScope.launch {
            _gameMode.collect { ordinal ->
                val mode = GameMod.values()[ordinal]

                gameMode = mode

                when (mode) {
                    GameMod.Computer -> {
                        playerOne = Players.Player1
                        playerTwo = Players.Computer
                    }
                    GameMod.PvP -> {
                        playerOne = Players.Player1
                        playerTwo = Players.Player2
                    }
                }

                gameUtil.updatePlayer(
                    one = playerOne,
                    two = playerTwo
                )
            }
        }
    }

    fun updateBoard(row: Int, col: Int) {
        val index = board[0].size * row + col

        viewModelScope.launch {
            gameUtil.updateBoard(index)
        }
    }

    fun clearBoard() {
        winner = WinType.None

        viewModelScope.launch {
            gameUtil.clearBoard()
        }
    }

}