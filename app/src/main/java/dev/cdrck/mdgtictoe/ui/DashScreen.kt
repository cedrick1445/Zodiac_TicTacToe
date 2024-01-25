package dev.cdrck.mdgtictoe.ui


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.cdrck.mdgtictoe.R
import dev.cdrck.mdgtictoe.comps.ModeSelector
import dev.cdrck.mdgtictoe.datas.GameDestination
import dev.cdrck.mdgtictoe.datas.GameMod

@Composable
fun DashScreen(
	navController: NavController,
	viewModel: DashVM
) {

	Box(modifier = Modifier.fillMaxSize()){
		Image(
			painter = painterResource(id = R.drawable.dash_background),
			contentDescription = "Dash background",
			contentScale = ContentScale.FillBounds,
			modifier = Modifier.matchParentSize()
		)
	}


	val playButtonEnabled = remember(viewModel.selectedGameMode) {
		return@remember true
	}

	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center,
		modifier = Modifier
			.fillMaxSize()
	) {
		Image(
			painter = painterResource(id = R.drawable.zodtic_app_icon),
			contentDescription = "Application Logo",
			modifier = Modifier
				.size(101.dp)
				.clip(MaterialTheme.shapes.large)
		)
		
		Spacer(modifier = Modifier.padding(8.dp))
		
		ModeSelector(
			gameModes = GameMod.values(),
			selectedGameMode = viewModel.selectedGameMode,
			onGameModeChanged = viewModel::updateGameMode,
			modifier = Modifier
				.fillMaxWidth(0.6f),

		)
		
		Spacer(modifier = Modifier.padding(8.dp))

		ElevatedButton(
			enabled = playButtonEnabled,
			onClick = {
				navController.navigate(
					GameDestination.Game.Home.createRoute(
						gameMode = viewModel.selectedGameMode
					)
				)
			}
		) {
			Text(
				"Play",
				style = MaterialTheme.typography.bodyLarge.copy(
					fontWeight = FontWeight.Bold
				),
				color = MaterialTheme.colorScheme.primary)

		}
		
		Spacer(modifier = Modifier.padding(16.dp))

	}
}