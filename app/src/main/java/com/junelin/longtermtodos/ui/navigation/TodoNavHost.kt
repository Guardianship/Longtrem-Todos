package com.junelin.longtermtodos.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.junelin.longtermtodos.ui.addtask.AddTaskScreen
import com.junelin.longtermtodos.ui.category.CategoryManageScreen
import com.junelin.longtermtodos.ui.home.HomeScreen
import com.junelin.longtermtodos.ui.settings.SettingsScreen

object Routes {
    const val HOME = "home"
    const val ADD_TASK = "add_task"
    const val EDIT_TASK = "edit_task/{taskId}"
    const val SETTINGS = "settings"
    const val CATEGORIES = "categories"

    fun editTask(taskId: Long) = "edit_task/$taskId"
}

@Composable
fun TodoNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onAddTask = { navController.navigate(Routes.ADD_TASK) },
                onEditTask = { taskId ->
                    navController.navigate(Routes.editTask(taskId))
                },
                onSettings = { navController.navigate(Routes.SETTINGS) },
                onManageCategories = { navController.navigate(Routes.CATEGORIES) }
            )
        }

        composable(Routes.ADD_TASK) {
            AddTaskScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.EDIT_TASK,
            arguments = listOf(navArgument("taskId") { type = NavType.LongType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId") ?: 0L
            AddTaskScreen(
                taskId = taskId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onManageCategories = { navController.navigate(Routes.CATEGORIES) }
            )
        }

        composable(Routes.CATEGORIES) {
            CategoryManageScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
