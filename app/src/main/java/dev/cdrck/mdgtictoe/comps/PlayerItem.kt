package dev.cdrck.mdgtictoe.comps

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.cdrck.mdgtictoe.R
import dev.cdrck.mdgtictoe.datas.PoinType
import dev.cdrck.mdgtictoe.players.Players
import dev.cdrck.mdgtictoe.themes.light_onFlirtContainer


@Composable
fun PlayerItem(
	player: Players,
	playerTurn: Boolean,
	modifier: Modifier = Modifier
) {
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = Modifier.padding(0.dp,50.dp,0.dp,0.dp).semantics {  this.contentDescription = "Players Item" }
	) {
		Card(modifier = modifier) {
			Column(
				horizontalAlignment = Alignment.CenterHorizontally,
				modifier = Modifier.padding(18.dp)
			) {
				Text(
					text = player.name,
					style = MaterialTheme.typography.bodyMedium.copy(
						fontWeight = FontWeight.Bold
					)
				)

				Spacer(modifier = Modifier.height(16.dp))
				
				PlayerPointType(
					playerTurn = playerTurn,
					pointType = player.pointType
				)
			}
		}
		
		Spacer(modifier = Modifier.height(8.dp))
		
		Card(
			shape = CircleShape,
			border = BorderStroke(
				width = 1.dp,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)
		) {
			Text(
				text = "Win rounds: ${player.win}",
				style



				= MaterialTheme.typography.bodyMedium.copy(
					fontWeight = FontWeight.Medium,
					color = MaterialTheme.colorScheme.tertiary
				),
				modifier = Modifier
					.padding(
						horizontal = 8.dp,
						vertical = 4.dp
				)
			)
		}
	}
}

@Composable
private fun PlayerPointType(
	playerTurn: Boolean,
	pointType: PoinType
) {

	val pointTypeBackground by animateColorAsState(
		targetValue = if (playerTurn) light_onFlirtContainer
		else MaterialTheme.colorScheme.background,
		animationSpec = tween(500), label = ""
	)

	Box(
		modifier = Modifier
			.clip(MaterialTheme.shapes.large)
			.drawBehind {
				drawRoundRect(
					color = pointTypeBackground,
					cornerRadius = CornerRadius(
						x = CircleShape.topStart.toPx(size, this),
						y = CircleShape.bottomEnd.toPx(size, this)
					)
				)
			}
			.semantics {  this.contentDescription = "Players" }
	) {
		PlayerPointTypeImage(pointType = pointType)
	}
}

@Composable
private fun PlayerPointTypeImage(
	pointType: PoinType
) {
	Image(
		painter = painterResource(
			id = when (pointType) {
				PoinType.Empty -> R.drawable.transparent
				PoinType.X -> R.drawable.ic_tic_tac_toe_x
				PoinType.O -> R.drawable.ic_tic_tac_toe_o
			}
		),
		contentDescription = "Players",
		modifier = Modifier
			.padding(
				horizontal = 8.dp,
				vertical = 4.dp
			)
			.size(38.dp)
	)
}
