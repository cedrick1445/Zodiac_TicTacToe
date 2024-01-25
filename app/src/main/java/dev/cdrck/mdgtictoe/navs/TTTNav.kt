package dev.cdrck.mdgtictoe.navs

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dev.cdrck.mdgtictoe.datas.GameDestination

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TTTNav() {
	
	val navController = rememberAnimatedNavController()
	val systemUiController = rememberSystemUiController()
	
	SideEffect {
		systemUiController.setSystemBarsColor(
			color = Color.Transparent,
			darkIcons = true
		)
	}
	
	AnimatedNavHost(
		navController = navController,
		startDestination = GameDestination.Dashboard.Root.route,
		modifier = Modifier
			.fillMaxSize()
	) {
		DashAnimNav(navController)
		
		GameAnimNav(navController)
	}
	
}
