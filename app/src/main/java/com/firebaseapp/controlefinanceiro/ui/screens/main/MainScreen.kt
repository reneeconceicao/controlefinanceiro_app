package com.firebaseapp.controlefinanceiro.ui.screens.main

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.firebaseapp.controlefinanceiro.ui.navigation.MenuDestination
import com.firebaseapp.controlefinanceiro.ui.screens.graphics.GraphicsScreen
import com.firebaseapp.controlefinanceiro.ui.screens.home.HomeScreen
import com.firebaseapp.controlefinanceiro.ui.screens.reports.ReportScreen
import com.firebaseapp.controlefinanceiro.ui.screens.settings.SettingsScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navigateToRegister: () -> Unit,
    navigateToEdit: (Int) -> Unit,
    navigateToEditRecurring: (Int, Long?) -> Unit,
    navigateExpenseCategories: () -> Unit,
    navigateIncomeCategories: () -> Unit,
) {

    val pagerState = rememberPagerState(pageCount = { MenuDestination.entries.size })

    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            Column() {
                HorizontalDivider()
                PrimaryTabRow(
                    pagerState.currentPage,
                    Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .navigationBarsPadding(),
                    indicator = {
                    },
                    divider = {
                    }
                ) {
                    MenuDestination.entries.forEachIndexed { index, destination ->

                        val selected = pagerState.currentPage == index
                        CompositionLocalProvider(
                            LocalRippleConfiguration provides null
                        ) {
                            Tab(
                                selected = selected,
                                onClick = {
                                    scope.launch {
                                        pagerState.scrollToPage(index)
                                    }
                                },
                                icon = {
                                    val scale by animateFloatAsState(
                                        targetValue = if (selected) 1.15f else 1f,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessMedium
                                        ),
                                        label = "iconScale"
                                    )

                                    Icon(
                                        destination.icon,
                                        contentDescription = stringResource(destination.contentDescription),
                                        tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                        modifier = Modifier.graphicsLayer {
                                            scaleX = scale
                                            scaleY = scale
                                        }
                                    )
                                },
                                text = {
                                    Text(
                                        stringResource(destination.label),
                                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (selected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.outline
                                    )
                                },
                                modifier = Modifier.background(MaterialTheme.colorScheme.background)
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->

        MenuNavPager(
            modifier = Modifier.padding(innerPadding),
            pagerState = pagerState,
            navigateToRegister = navigateToRegister,
            navigateToEdit = navigateToEdit,
            navigateToEditRecurring = navigateToEditRecurring,
            navigateExpenseCategories = navigateExpenseCategories,
            navigateIncomeCategories = navigateIncomeCategories
        )
    }
}

@Composable
fun MenuNavPager(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    navigateToRegister: () -> Unit,
    navigateToEdit: (Int) -> Unit,
    navigateToEditRecurring: (Int, Long?) -> Unit,
    navigateExpenseCategories: () -> Unit,
    navigateIncomeCategories: () -> Unit,
) {
    HorizontalPager(
        state = pagerState,
        modifier = modifier
    ) { page ->

        Column() {


            when (page) {
                0 -> {
                    HomeScreen(
                        navigateToRegister = navigateToRegister,
                        navigateToEdit = navigateToEdit,
                        navigateToEditRecurring = navigateToEditRecurring,
                    )
                }

                1 -> {
                    ReportScreen()
                }

                2 -> {
                    GraphicsScreen()
                }

                3 -> {
                    SettingsScreen(
                        navigateExpenseCategories = navigateExpenseCategories,
                        navigateIncomeCategories = navigateIncomeCategories
                    )
                }

            }
        }
    }
}