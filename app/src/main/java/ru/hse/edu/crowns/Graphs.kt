package ru.hse.edu.crowns

import kotlinx.serialization.Serializable

@Serializable
data object AuthGraph {

    @Serializable
    data object LoginScreen

    @Serializable
    data object RegistrationScreen
}

@Serializable
data object GamesGraph {

    @Serializable
    data object AllGamesScreen

    @Serializable
    data class GameScreen(val difficulty: String)
}