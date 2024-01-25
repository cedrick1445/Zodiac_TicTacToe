package dev.cdrck.mdgtictoe.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.cdrck.mdgtictoe.comps.PlayerItem
import dev.cdrck.mdgtictoe.comps.RoundItem
import dev.cdrck.mdgtictoe.comps.TTTBoard
import dev.cdrck.mdgtictoe.datas.PoinType
import dev.cdrck.mdgtictoe.datas.TurnType
import dev.cdrck.mdgtictoe.datas.WinType
import dev.cdrck.mdgtictoe.exts.toast
import dev.cdrck.mdgtictoe.R

@Composable
fun GameScreen(
	navController: NavController,
	viewModel: GameVM
) {

	Box(modifier = Modifier
		.fillMaxSize()
		.semantics {  this.contentDescription = "App background" }){
		Image(
			painter = painterResource(id = R.drawable.zodtic_background),
			contentDescription = "App background",
			contentScale = ContentScale.FillBounds,
			modifier = Modifier.matchParentSize()
		)
	}

	val context = LocalContext.current

	LaunchedEffect(viewModel.winner) {
		when (viewModel.winner) {
			WinType.Tie -> "Tie".toast(context)
			WinType.O -> {
				if (viewModel.playerOne.pointType == PoinType.O) "${viewModel.playerOne.name} win".toast(context)
				else "${viewModel.playerTwo.name} win".toast(context)
			}
			WinType.X -> {
				if (viewModel.playerOne.pointType == PoinType.X) "${viewModel.playerOne.name} win".toast(context)
				else "${viewModel.playerTwo.name} win".toast(context)
			}
			WinType.None -> {}
		}

		viewModel.clearBoard()
	}

	Column(
		modifier = Modifier
			.systemBarsPadding()
			.fillMaxSize()
			.semantics {  this.contentDescription = "Tic Tac Toe Game" }
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween,
			modifier = Modifier
				.padding(16.dp)
				.fillMaxWidth()
				.semantics {  this.contentDescription = "Tic Tac Toe Game" }
		) {
			PlayerItem(
				player = viewModel.playerOne,
				playerTurn = viewModel.currentTurn == TurnType.PlayerOne
			)

			RoundItem(
				round = viewModel.round,
				draw = viewModel.draw
			)

			PlayerItem(
				player = viewModel.playerTwo,
				playerTurn = viewModel.currentTurn == TurnType.PlayerTwo
			)
		}

		Box(
			contentAlignment = Alignment.Center,
			modifier = Modifier
				.weight(1f)
				.semantics {  this.contentDescription = "Tic Tac Toe Box" }
		) {
			TTTBoard(
				board = viewModel.board,
				onClick = { row, col ->
					viewModel.updateBoard(row, col)
				},
				modifier = Modifier
					.fillMaxWidth()
					.aspectRatio(1f / 1f)
					.background(MaterialTheme.colorScheme.surface)
					.semantics {  this.contentDescription = "Tic Tac Toe Board" }
			)
		}
	}
}
