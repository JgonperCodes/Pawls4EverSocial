package juditgp.com.pawls4eversocial.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.pawls4ever.navigation.BottomNavigationBar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.accompanist.swiperefresh.*
import juditgp.com.pawls4eversocial.model.User
import juditgp.com.pawls4eversocial.model.Note
import juditgp.com.pawls4eversocial.model.resources.ui.NoteCardWithUserData
import juditgp.com.pawls4eversocial.viewmodel.NoteViewModel
import kotlin.text.get

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicMemoryScreen(navController: NavController) {
    var searchText by remember { mutableStateOf("") }
    var users by remember { mutableStateOf(listOf<User>()) }
    var notes by remember { mutableStateOf(listOf<Note>()) }
    var isRefreshing by remember { mutableStateOf(false) }
    val noteViewModel = remember { NoteViewModel() }

    // Cargar todas las notas públicas
    fun fetchAllPublicNotes() {
        val db = FirebaseFirestore.getInstance()
        db.collection("notes")
            .whereEqualTo("private", false)
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                notes = result.documents.mapNotNull { it.toObject(Note::class.java)?.copy(noteId = it.id) }
                users = emptyList()
            }
            .addOnFailureListener { exception ->
                println("Error al obtener notas públicas: ${exception.message}")
            }
    }

    // Buscar usuarios y notas públicas por título
    fun searchUsersAndNotes(query: String) {
        val db = FirebaseFirestore.getInstance()
        // Buscar usuarios por nombre
        db.collection("users")
            .orderBy("name")
            .startAt(query)
            .endAt(query + "\uf8ff")
            .get()
            .addOnSuccessListener { result ->
                users = result.documents.mapNotNull { it.toObject(User::class.java)?.copy(userId = it.id) }
            }
        // Buscar notas públicas por título
        db.collection("notes")
            .whereEqualTo("private", false)
            .get()
            .addOnSuccessListener { result ->
                notes = result.documents.mapNotNull { it.toObject(Note::class.java)?.copy(noteId = it.id) }
                    .filter { it.title.contains(query, ignoreCase = true) }
            }
    }

    LaunchedEffect(Unit) {
        fetchAllPublicNotes()
    }

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = {
            isRefreshing = true
            if (searchText.isBlank()) {
                fetchAllPublicNotes()
            } else {
                searchUsersAndNotes(searchText)
            }
            isRefreshing = false
        }
    ) {
        Scaffold(
            bottomBar = { BottomNavigationBar(navController = navController) },
            topBar = {
                TextField(
                    value = searchText,
                    onValueChange = {
                        searchText = it
                        if (it.isBlank()) {
                            fetchAllPublicNotes()
                        } else {
                            searchUsersAndNotes(it)
                        }
                    },
                    placeholder = { Text("Buscar usuarios o notas...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    singleLine = true,
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFDF1E7))
                    .padding(innerPadding)
            ) {
                if (searchText.isNotBlank() && users.isNotEmpty()) {
                    item {
                        Text(
                            text = "Usuarios encontrados",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    items(users) { user ->
                        ProfileUserCard(user, navController)
                    }
                }
                if (notes.isNotEmpty()) {
                    item {
                        Text(
                            text = if (searchText.isBlank()) "Notas públicas" else "Notas encontradas",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    items(notes) { note ->
                        NoteCardWithUserData(
                            noteId = note.noteId,
                            noteImages = note.images ?: emptyList(),
                            noteDate = note.date.toString(),
                            noteTitle = note.title,
                            noteContent = note.content,
                            likeCount = note.likes?.size ?: 0,
                            commentCount = note.comments?.size ?: 0,
                            isLiked = false,
                            userIdActual = "", // Ajusta según tu lógica
                            navController = navController,
                            noteViewModel = noteViewModel
                        )
                    }
                }
                if (searchText.isNotBlank() && users.isEmpty() && notes.isEmpty()) {
                    item {
                        Text(
                            text = "No se encontraron resultados.",
                            color = Color.Gray,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp)
                        )
                    }
                }
            }
        }
    }
}