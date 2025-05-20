package com.example.pawls4ever.navigation

import ProfileUserScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import juditgp.com.pawls4eversocial.view.FollowingScreen
import juditgp.com.pawls4eversocial.view.HomeScreen
import juditgp.com.pawls4eversocial.view.LoginScreen
import juditgp.com.pawls4eversocial.view.NewMemoryScreen
import juditgp.com.pawls4eversocial.view.NotificationScreen
import juditgp.com.pawls4eversocial.view.PetsScreen
import juditgp.com.pawls4eversocial.view.PublicMemoryScreen
import juditgp.com.pawls4eversocial.view.SingUpScreen
import juditgp.com.pawls4eversocial.view.SplashScreen
import juditgp.com.pawls4eversocial.view.UsersMemoryScreen
@Composable
fun Navigation() {
    val navController = rememberNavController()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val context = androidx.compose.ui.platform.LocalContext.current
    val userId = currentUser?.uid?.takeIf { it.isNotEmpty() }
        ?: run {
            val prefs = context.getSharedPreferences("UserPrefs", android.content.Context.MODE_PRIVATE)
            prefs.getString("userId", "") ?: ""
        }
    NavHost(
        navController = navController,
        startDestination = Screens.SplashScreen.name
    ) {
        composable(Screens.PublicMemoryScreen.name) {
            PublicMemoryScreen(navController = navController)
        }
        composable(Screens.SplashScreen.name) {
            SplashScreen(navController = navController)
        }
        composable(Screens.LoginScreen.name) {
            LoginScreen(navController = navController)
        }
        composable(Screens.RegisterScreen.name) {
            SingUpScreen(navController = navController)
        }
        composable(Screens.HomeScreen.name) {
            HomeScreen(
                navController = navController,
                userId = userId
            )
        }
        composable(
            route = "${Screens.ProfileUserScreen.name}/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userIdFromArgs = backStackEntry.arguments?.getString("userId") ?: ""
            ProfileUserScreen(navController = navController, userId = userIdFromArgs)
        }
        composable(Screens.NotificationScreen.name) {
            NotificationScreen(navController = navController, userId = userId)
        }
        composable(
            route = "PetsScreen/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            PetsScreen(navController = navController, userId = userId)
        }
        composable(
            route = "${Screens.UsersMemoryScreen.name}/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userIdFromArgs = backStackEntry.arguments?.getString("userId") ?: ""
            UsersMemoryScreen(navController = navController, userId = userIdFromArgs)
        }

        composable(
            route = "${Screens.PetsScreen.name}/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userIdFromArgs = backStackEntry.arguments?.getString("userId") ?: ""
            PetsScreen(navController = navController, userId = userIdFromArgs)
        }

        composable(
            route = "FollowingScreen/{userId}?isFollow={isFollow}",
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType },
                navArgument("isFollow") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val isFollow = backStackEntry.arguments?.getBoolean("isFollow") ?: false
            FollowingScreen(userId = userId, isFollow = isFollow, navController = navController)
        }
        composable(
            route = "${Screens.NewMemoryScreen.name}/{userId}?isViewing={isViewing}&noteId={noteId}",
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType },
                navArgument("isViewing") { type = NavType.BoolType; defaultValue = false },
                navArgument("noteId") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val userIdFromArgs = backStackEntry.arguments?.getString("userId") ?: ""
            val isViewing = backStackEntry.arguments?.getBoolean("isViewing") ?: false
            val noteId = backStackEntry.arguments?.getString("noteId")
            NewMemoryScreen(
                navController = navController,
                userId = userIdFromArgs,
                isViewing = isViewing,
                noteId = noteId
            )
        }
    }


}

