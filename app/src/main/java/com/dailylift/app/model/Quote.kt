package com.dailylift.app.model

import androidx.compose.ui.graphics.Color
import java.util.UUID

/**
 * Quote categories with display properties.
 */
enum class QuoteCategory(
    val displayName: String,
    val icon: String, // Material icon name reference
    val gradientColors: List<Color>,
    val accentColor: Color
) {
    SUCCESS(
        displayName = "Success",
        icon = "star",
        gradientColors = listOf(Color(0xFFFFD68F), Color(0xFFFFA666)),
        accentColor = Color(0xFFE68C33)
    ),
    LOVE(
        displayName = "Love",
        icon = "heart",
        gradientColors = listOf(Color(0xFFFFBFCC), Color(0xFFF28CB3)),
        accentColor = Color(0xFFD94D80)
    ),
    LIFE(
        displayName = "Life",
        icon = "leaf",
        gradientColors = listOf(Color(0xFF99E6C7), Color(0xFF59BFA6)),
        accentColor = Color(0xFF33A68C)
    ),
    HUSTLE(
        displayName = "Hustle",
        icon = "bolt",
        gradientColors = listOf(Color(0xFFB3A6FF), Color(0xFF8073F2)),
        accentColor = Color(0xFF7359E6)
    ),
    MINDSET(
        displayName = "Mindset",
        icon = "brain",
        gradientColors = listOf(Color(0xFFA6D9FF), Color(0xFF66A6F2)),
        accentColor = Color(0xFF408CE6)
    ),
    HAPPINESS(
        displayName = "Happiness",
        icon = "sun",
        gradientColors = listOf(Color(0xFFFFEB99), Color(0xFFFFC759)),
        accentColor = Color(0xFFD9A61A)
    );
}

/**
 * A motivational quote.
 */
data class Quote(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val author: String,
    val category: QuoteCategory
)
