package com.example.pawls4ever.navigation
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.unit.dp
import juditgp.com.pawls4eversocial.ui.theme.DarkGreen
import juditgp.com.pawls4eversocial.ui.theme.DarkRed


@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        "User Profile" to Icons.Filled.Person,
        "New Memory" to Icons.Filled.Create,
        "Home" to Icons.Filled.Home,
        "Notifications" to Icons.Filled.Notifications,
        "Public Memorys" to Icons.Filled.CollectionsBookmark,
        "My Memories" to Icons.Filled.Book
    )

    //Para actualizar los colores con la ruta actual de pantallas
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination
    val currentRoute = currentDestination?.route?.substringBefore("/")
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid ?: ""

    NavigationBar(containerColor = DarkGreen) {
        items.forEachIndexed { index, item ->
            val route = when (index) {
                0 -> Screens.ProfileUserScreen.name
                1 -> Screens.NewMemoryScreen.name
                2 -> Screens.HomeScreen.name
                3 -> Screens.NotificationScreen.name
                4 -> Screens.PublicMemoryScreen.name
                5 -> Screens.UsersMemoryScreen.name
                else -> ""
            }

            val isSelected = currentRoute == route

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(
                        when (index) {
                            0 -> "${Screens.ProfileUserScreen.name}/$userId"
                            1 ->"${Screens.NewMemoryScreen.name}/{userId}?isViewing={isViewing}&noteId={noteId}"
                            2 -> Screens.HomeScreen.name
                            3 -> Screens.NotificationScreen.name
                            4 -> Screens.PublicMemoryScreen.name
                            5 -> "${Screens.UsersMemoryScreen.name}/$userId"
                            else -> ""
                        }
                    ) {
                        popUpTo(Screens.HomeScreen.name) { inclusive = true }
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.second,
                        contentDescription = item.first,
                        modifier = Modifier.size(30.dp)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = DarkRed,
                    unselectedIconColor = Color.White,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}