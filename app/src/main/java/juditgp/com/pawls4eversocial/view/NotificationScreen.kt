package juditgp.com.pawls4eversocial.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import juditgp.com.pawls4eversocial.viewmodel.NotificationViewModel
import juditgp.com.pawls4eversocial.model.Notification
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pawls4ever.navigation.BottomNavigationBar
import juditgp.com.pawls4eversocial.ui.theme.DarkGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    navController: NavController,
    userId: String,
    viewModel: NotificationViewModel = viewModel()
) {
    val notifications by viewModel.notifications.collectAsState()

    LaunchedEffect(userId) {
        viewModel.loadNotifications(userId)
    }

    Scaffold(
        containerColor = DarkGreen, // DarkGreen
        bottomBar = { BottomNavigationBar(navController = navController) },
        topBar = {
            TopAppBar(
                title = { Text("Notificaciones", color = Color.White) },
                actions = {
                    if (notifications.isNotEmpty()) {
                        Button(onClick = { viewModel.markAllAsRead() }) {
                            Text("Marcar todas como vistas")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkGreen
                )
            )
        }
    ) { padding ->
        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "\uD83D\uDE1E", // Emoji de cara triste
                        fontSize = 48.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No tienes notificaciones",
                        color = Color.White
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(notifications) { notification ->
                    NotificationItem(
                        notification = notification,
                        onMarkAsRead = { viewModel.markAsRead(notification.notificationId) },
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationItem(
    notification: Notification,
    onMarkAsRead: () -> Unit,
    navController: NavController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.seen) Color(0xFFB2C8A7) else Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Texto de la notificación
                Text(
                    text = notification.content,
                    color = if (notification.seen) Color.Gray else Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    // Botón para ir al perfil del usuario que generó la notificación
                    if (notification.userProvider != null) {
                        TextButton(onClick = {
                            val userId = notification.userProvider.id
                            navController.navigate("ProfileUserScreen/$userId")
                        }) {
                            Text("Ver perfil", fontSize = 12.sp)
                        }
                    }
                    // Botón para ir a la nota relacionada si es like o comentario
                    if (notification.noteRelated != null && (notification.idLike != null || notification.idComment != null)) {
                        TextButton(onClick = {
                            val noteId = notification.noteRelated.id
                            navController.navigate("NoteDetailScreen/$noteId")
                        }) {
                            Text("Ver nota", fontSize = 12.sp)
                        }
                    }
                }
            }
            if (!notification.seen) {
                Button(onClick = onMarkAsRead) {
                    Text("Visto", fontSize = 12.sp)
                }
            }
        }
    }
}