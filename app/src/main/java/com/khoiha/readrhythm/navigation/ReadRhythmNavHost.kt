package com.khoiha.readrhythm.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.khoiha.readrhythm.data.DiscoverRepository
import com.khoiha.readrhythm.data.ReadingRepository
import com.khoiha.readrhythm.ui.bookdetail.BookDetailScreen
import com.khoiha.readrhythm.ui.bookdetail.BookDetailViewModel
import com.khoiha.readrhythm.ui.discover.DiscoverScreen
import com.khoiha.readrhythm.ui.discover.DiscoverViewModel
import com.khoiha.readrhythm.ui.insights.InsightsScreen
import com.khoiha.readrhythm.ui.insights.InsightsViewModel
import com.khoiha.readrhythm.ui.library.LibraryScreen
import com.khoiha.readrhythm.ui.library.LibraryViewModel

@Composable
fun ReadRhythmNavHost(
    readingRepository: ReadingRepository,
    discoverRepository: DiscoverRepository
) {
    val navController = rememberNavController()
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry.value?.destination
    val showBottomBar = currentDestination?.route in bottomNavigationRoutes.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavigationRoutes.forEach { destination ->
                        val selected = currentDestination
                            ?.hierarchy
                            ?.any { it.route == destination.route } == true

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Text(text = destination.shortLabel)
                            },
                            label = {
                                Text(text = stringResource(destination.labelRes))
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ReadRhythmRoute.Library.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(ReadRhythmRoute.Library.route) {
                val viewModel: LibraryViewModel = viewModel(
                    factory = LibraryViewModel.Factory(readingRepository)
                )
                val uiState by viewModel.uiState.collectAsState()

                LibraryScreen(
                    uiState = uiState,
                    onAddBook = viewModel::addBook,
                    onDeleteBook = viewModel::deleteBook,
                    onBookClick = { book ->
                        navController.navigate(ReadRhythmRoute.BookDetail.createRoute(book.id))
                    },
                    onSearchQueryChange = viewModel::updateSearchQuery,
                    onFormatFilterChange = viewModel::updateFormatFilter
                )
            }
            composable(ReadRhythmRoute.Discover.route) {
                val viewModel: DiscoverViewModel = viewModel(
                    factory = DiscoverViewModel.Factory(
                        discoverRepository = discoverRepository,
                        readingRepository = readingRepository
                    )
                )
                val uiState by viewModel.uiState.collectAsState()

                DiscoverScreen(
                    uiState = uiState,
                    onSearch = viewModel::search,
                    onAddToLibrary = viewModel::addToLibrary
                )
            }
            composable(ReadRhythmRoute.Insights.route) {
                val viewModel: InsightsViewModel = viewModel(
                    factory = InsightsViewModel.Factory(readingRepository)
                )
                val uiState by viewModel.uiState.collectAsState()

                InsightsScreen(uiState = uiState)
            }
            composable(
                route = ReadRhythmRoute.BookDetail.route,
                arguments = listOf(
                    navArgument(ReadRhythmRoute.BookDetail.bookIdArg) {
                        type = NavType.LongType
                    }
                )
            ) { entry ->
                val bookId = entry.arguments?.getLong(ReadRhythmRoute.BookDetail.bookIdArg) ?: 0L
                val viewModel: BookDetailViewModel = viewModel(
                    factory = BookDetailViewModel.Factory(
                        readingRepository = readingRepository,
                        bookId = bookId
                    )
                )
                val uiState by viewModel.uiState.collectAsState()

                BookDetailScreen(
                    uiState = uiState,
                    onBack = { navController.popBackStack() },
                    onAddSession = viewModel::addSession
                )
            }
        }
    }
}
