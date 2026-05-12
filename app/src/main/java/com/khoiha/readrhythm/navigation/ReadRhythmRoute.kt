package com.khoiha.readrhythm.navigation

sealed class ReadRhythmRoute(
    val route: String,
    val label: String,
    val shortLabel: String
) {
    data object Library : ReadRhythmRoute(
        route = "library",
        label = "Library",
        shortLabel = "R"
    )

    data object Sessions : ReadRhythmRoute(
        route = "sessions",
        label = "Sessions",
        shortLabel = "S"
    )

    data object Insights : ReadRhythmRoute(
        route = "insights",
        label = "Insights",
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
    ReadRhythmRoute.Sessions,
    ReadRhythmRoute.Insights
)
