package ru.hse.edu.crowns

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.hse.edu.components.presentation.Difficulty
import ru.hse.edu.crowns.presentation.AllGamesScreen
import ru.hse.edu.crowns.presentation.GameScreen
import ru.hse.edu.crowns.presentation.LoginScreen
import ru.hse.edu.crowns.presentation.RegistrationScreen
import ru.hse.edu.crowns.ui.theme.AppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                MainNavigation()
            }
        }
    }
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    Scaffold { padding ->
        NavHost(
            navController = navController, startDestination = GamesGraph,
            enterTransition = { fadeIn(tween(200)) },
            exitTransition = { fadeOut(tween(200)) },
            popEnterTransition = { fadeIn(tween(200)) },
            popExitTransition = { fadeOut(tween(200)) },
            modifier = Modifier.padding(padding)
        ) {
            navigation<AuthGraph>(
                startDestination = AuthGraph.LoginScreen,
            ) {
                composable<AuthGraph.LoginScreen> {
                    LoginScreen(
                        onNavigateToRegistration = { navController.navigate(AuthGraph.RegistrationScreen) },
                        onNavigateToMainScreen = { navController.navigate(GamesGraph) }
                    )
                }
                composable<AuthGraph.RegistrationScreen> {
                    RegistrationScreen(
                        onNavigateToLogin = { navController.navigate(AuthGraph.LoginScreen) },
                        onNavigateToMainScreen = { navController.navigate(GamesGraph) }
                    )
                }
            }

            navigation<GamesGraph>(
                startDestination = GamesGraph.AllGamesScreen,
            ) {
                composable<GamesGraph.AllGamesScreen> {
                    AllGamesScreen { navController.navigate(GamesGraph.GameScreen(it.label)) }
                }

                composable<GamesGraph.GameScreen> {
                    val difficulty = Difficulty.valueOf(it.arguments?.getString("difficulty"))
                    GameScreen(
                        difficulty,
                        onTimeEnd = {},
                        onExit = { navController.popBackStack() },
                    )
                }
            }
        }
    }
}
