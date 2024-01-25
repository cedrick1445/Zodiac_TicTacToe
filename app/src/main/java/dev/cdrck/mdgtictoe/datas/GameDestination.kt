package dev.cdrck.mdgtictoe.datas


import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class GameDestination(val route: String) {

    class Dashboard {
        object Root: GameDestination("dashboard/root")
        object Home: GameDestination("dashboard/home")
    }

    class Score {
        object Root: GameDestination("score/root")
        object Home: GameDestination("score/home")
    }

    class Game {
        object Root: GameDestination("game/root")
        object Home: GameDestination(
            route = "game/home?" +
                    "$ARG_GAME_MODE={$ARG_GAME_MODE}"
        ) {
            fun createRoute(gameMode: GameMod): String {
                return "game/home?" +
                        "$ARG_GAME_MODE=${gameMode.ordinal}"
            }

            val arguments = listOf(
                navArgument(ARG_GAME_MODE) {
                    type = NavType.IntType
                }
            )
        }
    }

}

const val ARG_GAME_MODE = "game_mode"
