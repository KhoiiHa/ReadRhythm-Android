package com.khoiha.readrhythm.navigation

import androidx.annotation.StringRes
import com.khoiha.readrhythm.R

sealed class ReadRhythmRoute(
    val route: String,
    @param:StringRes val labelRes: Int,
    val shortLabel: String
) {
    data object Library : ReadRhythmRoute(
        route = "library",
        labelRes = R.string.nav_library,
        shortLabel = "R"
    )

    data object Discover : ReadRhythmRoute(
        route = "discover",
        labelRes = R.string.nav_discover,
        shortLabel = "D"
    )

    data object Insights : ReadRhythmRoute(
        route = "insights",
        labelRes = R.string.nav_insights,
        shortLabel = "I"
    )

    data object BookDetail {
        const val bookIdArg = "bookId"
        const val route = "book/{$bookIdArg}"

        fun createRoute(bookId: Long): String {
            return "book/$bookId"
        }
    }
}

val bottomNavigationRoutes = listOf(
    ReadRhythmRoute.Library,
    ReadRhythmRoute.Discover,
    ReadRhythmRoute.Insights
)
