package juditgp.com.pawls4eversocial.view


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pawls4ever.navigation.BottomNavigationBar
import juditgp.com.pawls4eversocial.model.resources.ui.NoteCard
import juditgp.com.pawls4eversocial.model.resources.ui.TextTitleWithBackgroundRound
import juditgp.com.pawls4eversocial.viewmodel.HomeViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import juditgp.com.pawls4eversocial.R
import juditgp.com.pawls4eversocial.ui.theme.BackgroundColorLight
import juditgp.com.pawls4eversocial.ui.theme.DarkRed
import juditgp.com.pawls4eversocial.ui.theme.placeHolderNewNote
import juditgp.com.pawls4eversocial.viewmodel.NoteViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.ui.platform.LocalContext
import juditgp.com.pawls4eversocial.model.resources.ui.NoteCardWithUserData
import kotlin.text.format
import kotlin.text.get


@Composable
fun HomeScreen(
    navController: NavController,
    userId: String,
    homeViewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    noteViewModel: NoteViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val userLastPost by homeViewModel.userLastPost.collectAsState()
    val publicPosts by homeViewModel.publicPosts.collectAsState()
    val followedPosts by homeViewModel.followedPosts.collectAsState()
    val userFollowing by homeViewModel.userFollowing.collectAsState()
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    var isRefreshing by remember { mutableStateOf(false) }
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)
    val navBackStackEntry = navController.currentBackStackEntry
    val context = LocalContext.current

    LaunchedEffect(navBackStackEntry) {
        val refresh = navBackStackEntry?.savedStateHandle?.get<Boolean>("refreshHome") ?: false
        if (refresh) {
            homeViewModel.loadUserLastPost(userId)
            homeViewModel.loadUserFollowing(userId)
            homeViewModel.loadPublicPosts()
            homeViewModel.loadFollowedPosts()
            navBackStackEntry?.savedStateHandle?.set("refreshHome", false)
        }
    }
    LaunchedEffect(userId) {
        homeViewModel.loadUserLastPost(userId)
        homeViewModel.loadUserFollowing(userId)
        homeViewModel.loadPublicPosts()
    }
    LaunchedEffect(userFollowing) {
        homeViewModel.loadFollowedPosts()
    }

    Scaffold(
        contentColor = BackgroundColorLight,
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BackgroundColorLight)
                .padding(start = 8.dp, end = 8.dp, top = 8.dp)
        ) {
            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = {
                    isRefreshing = true
                    homeViewModel.loadUserLastPost(userId)
                    homeViewModel.loadUserFollowing(userId)
                    homeViewModel.loadPublicPosts()
                    homeViewModel.loadFollowedPosts()
                    isRefreshing = false
                }
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Última nota del usuario
                    item {
                        TextTitleWithBackgroundRound(
                            sectionTitle = "Último recuerdo añadido"
                        )
                        if (userLastPost != null) {
                            NoteCard(
                                noteId = (userLastPost!!["noteId"] as String).substringAfterLast("/"),
                                userName = userLastPost!!["userName"] as? String
                                    ?: "Usuario desconocido",
                                userProfileImage = userLastPost!!["images"]?.let { (it as List<String>).firstOrNull() }
                                    ?: "https://imgur.com/defaultProfileImage.jpg",
                                noteImages = userLastPost!!["images"] as List<String>?
                                    ?: emptyList(),
                                noteDate = userLastPost!!["date"]?.let {
                                    val timestamp = it as com.google.firebase.Timestamp
                                    dateFormatter.format(timestamp.toDate())
                                } ?: "Fecha desconocida",
                                noteTitle = userLastPost!!["title"] as? String ?: "Sin título",
                                likeCount = (userLastPost!!["likes"] as? List<*>)?.size ?: 0,
                                commentCount = (userLastPost!!["comments"] as? List<*>)?.size ?: 0,
                                isLiked = false,
                                noteContent = userLastPost!!["content"] as? String ?: "",
                                userIdActual = userId,
                                noteOwnerId = userLastPost!!["userId"] as? String ?: "",
                                navController = navController,
                                noteViewModel = noteViewModel
                            )
                        } else {
                            placeHolderNewNote(
                                text = "Haz click para añadir un nuevo recuerdo!",
                                modifierText = Modifier,
                                image = R.drawable.chicoconmovilyperro,
                                color = DarkRed,
                                contentDescriptionImage = "Imagen dueño con perro",
                                modifierImage = Modifier,
                                onClick = {
                                    navController.navigate("NewMemoryScreen/$userId?isViewing=true&noteId=")
                                }
                            )
                        }
                    }


// Entradas de los seguidos
                    if (userFollowing.isNotEmpty()) {
                        item {
                            TextTitleWithBackgroundRound(
                                sectionTitle = "Últimas entradas de los seguidos"
                            )
                        }
                        items(followedPosts) { post ->
                            NoteCardWithUserData(
                                noteId = (post["noteId"] as? String)?.substringAfterLast("/") ?: "",
                                noteImages = post["images"] as? List<String> ?: emptyList(),
                                noteDate = post["date"]?.let {
                                    val timestamp = it as? com.google.firebase.Timestamp
                                    timestamp?.toDate()?.let { date ->
                                        dateFormatter.format(date)
                                    }
                                } ?: "Fecha desconocida",
                                noteTitle = post["title"] as? String ?: "Sin título",
                                noteContent = post["content"] as? String ?: "", // <-- Añade esto
                                likeCount = (post["likes"] as? List<*>)?.size ?: 0,
                                commentCount = (post["comments"] as? List<*>)?.size ?: 0,
                                isLiked = false,
                                userIdActual = userId,
                                navController = navController,
                                noteViewModel = noteViewModel
                            )
                        }
                    }

                    // Entradas públicas de otros usuarios
                    item {
                        TextTitleWithBackgroundRound(
                            sectionTitle = "Últimas notas públicas"
                        )
                    }
                    items(publicPosts) { post ->
                        NoteCardWithUserData(
                            noteId = (post["noteId"] as? String)?.substringAfterLast("/") ?: "",
                            noteImages = post["images"] as? List<String> ?: emptyList(),
                            noteDate = post["date"]?.let {
                                val timestamp = it as? com.google.firebase.Timestamp
                                timestamp?.toDate()?.let { date ->
                                    dateFormatter.format(date)
                                }
                            } ?: "Fecha desconocida",
                            noteTitle = post["title"] as? String ?: "Sin título",
                            noteContent = post["content"] as? String ?: "", // <-- Añade esto
                            likeCount = (post["likes"] as? List<*>)?.size ?: 0,
                            commentCount = (post["comments"] as? List<*>)?.size ?: 0,
                            isLiked = false,
                            userIdActual = userId,
                            navController = navController,
                            noteViewModel = noteViewModel
                        )
                    }
                }
            }
        }
    }
}

