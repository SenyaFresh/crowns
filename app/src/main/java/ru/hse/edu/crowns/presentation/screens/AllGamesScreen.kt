package ru.hse.edu.crowns.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.hse.edu.components.presentation.Difficulty
import ru.hse.edu.components.presentation.GameCard
import ru.hse.edu.crowns.R
import ru.hse.edu.crowns.model.game.GameType

@Composable
fun AllGamesScreen(onGameClick: (Difficulty, GameType) -> Unit, onNavigateToProfile: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 30.dp)
            .background(color = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Игры",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            IconButton(
                modifier = Modifier
                    .height(38.dp)
                    .aspectRatio(1.6f),
                onClick = onNavigateToProfile,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(imageVector = Icons.Filled.Person, null)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier)
            GameCard(
                title = "Queens",
                gameIconPainter = painterResource(id = R.drawable.queens_green),
                onPlayClick = { difficulty -> onGameClick(difficulty, GameType.COLORED_QUEENS)}
            )
            GameCard(
                title = "Killer Sudoku",
                gameIconPainter = painterResource(id = R.drawable.killer_sudoku_green),
                onPlayClick = { difficulty -> onGameClick(difficulty, GameType.KILLER_SUDOKU)}
            )
            GameCard(
                title = "Tango",
                gameIconPainter = painterResource(id = R.drawable.tango_green),
                onPlayClick = { difficulty -> onGameClick(difficulty, GameType.TANGO)}
            )
            GameCard(
                title = "N Ферзей",
                gameIconPainter = painterResource(id = R.drawable.n_queens_green),
                onPlayClick = { difficulty -> onGameClick(difficulty, GameType.N_QUEENS)}
            )
            Spacer(modifier = Modifier)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AllGamesScreenPreview() {
    AllGamesScreen ({ _, _ -> }, {})
}