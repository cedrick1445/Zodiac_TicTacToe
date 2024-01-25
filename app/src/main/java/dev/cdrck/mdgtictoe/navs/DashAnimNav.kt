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
import dev.cdrck.mdgtictoe.ui.DashScreen
import dev.cdrck.mdgtictoe.ui.DashVM

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.DashAnimNav(navController: NavController) {
	navigation(
		startDestination = GameDestination.Dashboard.Home.route,
		route = GameDestination.Dashboard.Root.route
	) {
		composable(
			route = GameDestination.Dashboard.Home.route,
			enterTransition = { fadeIn() },
			exitTransition = { fadeOut() },
			popEnterTransition = { fadeIn() },
			popExitTransition = { fadeOut() }
		) { backEntry ->
			val viewModel = hiltViewModel<DashVM>(backEntry)
			
			DashScreen(
				navController = navController,
				viewModel = viewModel
			)
		}
	}
}

