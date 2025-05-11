package ru.hse.edu.crowns.model.game.bg

import ru.hse.edu.crowns.R

sealed class BackgroundEntity(
    val id: String,
    val imageResource: Int,
    val isDark: Boolean
) {
    data object ColorfulBackground: BackgroundEntity("colorful_bg", R.drawable.colorful_background, true)
    data object GeometricBackground: BackgroundEntity("geometric_bg", R.drawable.geometric_background, false)
    data object GradientBackground: BackgroundEntity("gradient_bg", R.drawable.gradient_background, false)
    data object HollowedBackground: BackgroundEntity("hollowed_bg", R.drawable.hollowed_background, true)
    data object RainbowBackground: BackgroundEntity("rainbow_bg", R.drawable.rainbow_background, false)
    data object SpaceBackground: BackgroundEntity("space_bg", R.drawable.space_background, true)

    companion object {
        fun fromId(id: String): BackgroundEntity? {
            return when (id) {
                ColorfulBackground.id -> ColorfulBackground
                GeometricBackground.id -> GeometricBackground
                GradientBackground.id -> GradientBackground
                HollowedBackground.id -> HollowedBackground
                RainbowBackground.id -> RainbowBackground
                SpaceBackground.id -> SpaceBackground
                else -> null
            }
        }

        fun getAllEntities(): List<BackgroundEntity> {
            return listOf(
                ColorfulBackground,
                GeometricBackground,
                GradientBackground,
                HollowedBackground,
                RainbowBackground,
                SpaceBackground
            )
        }
    }
}