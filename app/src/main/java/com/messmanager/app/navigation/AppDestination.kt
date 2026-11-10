package com.messmanager.app.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Assessment
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LocalGroceryStore
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.ui.graphics.vector.ImageVector
import com.messmanager.app.R

sealed class AppDestination(val route: String) {
    data object Home : AppDestination("home")
    data object Meal : AppDestination("meal")
    data object Bazar : AppDestination("bazar")
    data object Deposit : AppDestination("deposit")
    data object Report : AppDestination("report")
    data object Members : AppDestination("members")
    data object SharedExpense : AppDestination("shared_expense")
    data object Settings : AppDestination("settings")
}

data class BottomNavItem(
    val destination: AppDestination,
    @StringRes val labelRes: Int,
    val icon: ImageVector,
)

val bottomNavItems = listOf(
    BottomNavItem(AppDestination.Home, R.string.nav_home, Icons.Rounded.Home),
    BottomNavItem(AppDestination.Meal, R.string.nav_meal, Icons.Rounded.Restaurant),
    BottomNavItem(AppDestination.Bazar, R.string.nav_bazar, Icons.Rounded.LocalGroceryStore),
    BottomNavItem(AppDestination.Deposit, R.string.nav_deposit, Icons.Rounded.AccountBalanceWallet),
    BottomNavItem(AppDestination.Report, R.string.nav_report, Icons.Rounded.Assessment),
)
