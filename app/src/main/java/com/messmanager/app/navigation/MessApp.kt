package com.messmanager.app.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.messmanager.app.feature.bazar.BazarScreen
import com.messmanager.app.feature.deposit.DepositScreen
import com.messmanager.app.feature.home.HomeScreen
import com.messmanager.app.feature.meal.MealScreen
import com.messmanager.app.feature.members.MembersScreen
import com.messmanager.app.feature.report.ReportScreen
import com.messmanager.app.feature.settings.SettingsScreen
import com.messmanager.app.feature.shared_expense.SharedExpenseScreen

@Composable
fun MessApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val showBottomBar = bottomNavItems.any { it.destination.route == currentDestination?.route }

    val navigateToTopLevel = { destination: AppDestination ->
        navController.navigate(destination.route) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            if (showBottomBar) {
                AppBottomBar(
                    currentDestination = currentDestination,
                    onNavigate = navigateToTopLevel,
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppDestination.Home.route,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
        ) {
            composable(AppDestination.Home.route) {
                HomeScreen(
                    onOpenMembers = { navController.navigate(AppDestination.Members.route) },
                    onOpenSettings = { navController.navigate(AppDestination.Settings.route) },
                    onNavigateToMeal = { navigateToTopLevel(AppDestination.Meal) },
                    onNavigateToBazar = { navigateToTopLevel(AppDestination.Bazar) },
                    onNavigateToDeposit = { navigateToTopLevel(AppDestination.Deposit) },
                    onNavigateToSharedExpense = { navController.navigate(AppDestination.SharedExpense.route) },
                )
            }
            composable(AppDestination.Meal.route) {
                MealScreen(onOpenMembers = { navController.navigate(AppDestination.Members.route) })
            }
            composable(AppDestination.Bazar.route) { BazarScreen() }
            composable(AppDestination.Deposit.route) { DepositScreen() }
            composable(AppDestination.Report.route) {
                ReportScreen(
                    onOpenMembers = { navController.navigate(AppDestination.Members.route) },
                    onNavigateToSharedExpense = { navController.navigate(AppDestination.SharedExpense.route) },
                )
            }
            composable(AppDestination.Members.route) {
                MembersScreen(onBack = { navController.popBackStack() })
            }
            composable(AppDestination.SharedExpense.route) {
                SharedExpenseScreen(onBack = { navController.popBackStack() })
            }
            composable(AppDestination.Settings.route) {
                SettingsScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
