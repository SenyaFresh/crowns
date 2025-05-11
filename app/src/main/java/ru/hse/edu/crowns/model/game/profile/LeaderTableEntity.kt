package ru.hse.edu.crowns.model.game.profile

data class LeaderTableEntity(
    val position: Int,
    var nickname: String,
    val score: Long,
)