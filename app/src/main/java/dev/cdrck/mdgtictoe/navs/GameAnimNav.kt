package dev.cdrck.mdgtictoe.navs

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import dev.cdrck.mdgtictoe.datas.GameDestination
import dev.cdrck.mdgtictoe.ui.GameScreen
import dev.cdrck.mdgtictoe.ui.GameVM

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.GameAnimNav(navController: NavController) {
	navigation(
		startDestination = GameDestination.Game.Home.route,
		route = GameDestination.Game.Root.route
	) {
		composable(
			route = GameDestination.Game.Home.route,
			arguments = GameDestination.Game.Home.arguments,
			enterTransition = { fadeIn() },
			exitTransition = { fadeOut() },
			popEnterTransition = { fadeIn() },
			popExitTransition = { fadeOut() }
		) { backEntry ->
			val viewModel = hiltViewModel<GameVM>(backEntry)
			
			GameScreen(
				navController = navController,
				viewModel = viewModel
			)
		}
	}
}
