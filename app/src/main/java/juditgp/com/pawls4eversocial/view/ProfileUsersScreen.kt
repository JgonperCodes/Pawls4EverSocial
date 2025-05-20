
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.pawls4ever.navigation.BottomNavigationBar
import juditgp.com.pawls4eversocial.model.resources.ui.NoteCard
import juditgp.com.pawls4eversocial.viewmodel.NoteViewModel
import com.google.firebase.firestore.FirebaseFirestore
import juditgp.com.pawls4eversocial.model.UserProfileState
import juditgp.com.pawls4eversocial.viewmodel.ProfileUsersViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import juditgp.com.pawls4eversocial.model.resources.ui.NoteCardWithUserData
import juditgp.com.pawls4eversocial.model.resources.ui.TextTitleWithBackgroundRound
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pawls4ever.navigation.uploadImageToImgurComposable
import juditgp.com.pawls4eversocial.model.resources.ui.ClickableTextLabel
import juditgp.com.pawls4eversocial.model.resources.ui.MetricLabel
import juditgp.com.pawls4eversocial.model.resources.ui.PetCard

@Composable
fun ProfileUserScreen(
    navController: NavController,
    userId: String,
    viewModel: ProfileUsersViewModel = viewModel()
) {
    val state = viewModel.state
    var selectedTab by remember { mutableStateOf(0) }
    var showMenu by remember { mutableStateOf(false) }
    var editProfileMode by remember { mutableStateOf(false) }
    var uploadingProfileImage by remember { mutableStateOf(false) }
    var uploadingBannerImage by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val textFieldValueSaver = listSaver<TextFieldValue, Any>(
        save = { listOf(it.text, it.selection.start, it.selection.end) },
        restore = {
            TextFieldValue(
                text = it[0] as String,
                selection = androidx.compose.ui.text.TextRange(it[1] as Int, it[2] as Int)
            )
        }
    )

    var editName by rememberSaveable(stateSaver = textFieldValueSaver) { mutableStateOf(TextFieldValue(state.username)) }
    var editDesc by rememberSaveable(stateSaver = textFieldValueSaver) { mutableStateOf(TextFieldValue(state.description)) }
    var editProfileImg by rememberSaveable(stateSaver = textFieldValueSaver) { mutableStateOf(TextFieldValue(state.profileImageUrl)) }
    var editBannerImg by rememberSaveable(stateSaver = textFieldValueSaver) { mutableStateOf(TextFieldValue(state.bannerUrl)) }

    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var selectedProfileImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedBannerImageUri by remember { mutableStateOf<Uri?>(null) }

    val profileImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            uploadingProfileImage = true
            selectedProfileImageUri = uri
        }
    }
    val bannerImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            uploadingBannerImage = true
            selectedBannerImageUri = uri
        }
    }

    selectedProfileImageUri?.let { uri ->
        uploadImageToImgurComposable(uri) { imgurUrl ->
            if (imgurUrl != null) editProfileImg = TextFieldValue(imgurUrl)
            uploadingProfileImage = false
            selectedProfileImageUri = null
        }
    }
    selectedBannerImageUri?.let { uri ->
        uploadImageToImgurComposable(uri) { imgurUrl ->
            if (imgurUrl != null) editBannerImg = TextFieldValue(imgurUrl)
            uploadingBannerImage = false
            selectedBannerImageUri = null
        }
    }

    LaunchedEffect(userId, currentUserId) {
        viewModel.loadUserProfile(userId, currentUserId)
    }

    val noteViewModel = remember { NoteViewModel() }
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    Scaffold(
        containerColor = Color(0xFFF7F1E8),
        bottomBar = { BottomNavigationBar(navController) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(Color.LightGray)
                ) {
                    // Banner en modo edición: clic para seleccionar y subir nueva imagen
                    AsyncImage(
                        model = if (editProfileMode) editBannerImg.text else state.bannerUrl,
                        contentDescription = "Imagen de banner",
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(enabled = editProfileMode) { if (editProfileMode) bannerImageLauncher.launch("image/*") },
                        contentScale = ContentScale.Crop
                    )

// Subida automática a Imgur al seleccionar imagen
                    selectedBannerImageUri?.let { uri ->
                        uploadImageToImgurComposable(uri) { imgurUrl ->
                            if (imgurUrl != null) editBannerImg = TextFieldValue(imgurUrl)
                            uploadingBannerImage = false
                            selectedBannerImageUri = null
                        }
                    }
                    if (uploadingBannerImage) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0x88000000)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Subiendo imagen...", color = Color.White)
                        }
                    }
                }
            }
            // Foto de perfil y datos
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = if (editProfileMode) editProfileImg.text else state.profileImageUrl,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color.Gray, CircleShape)
                            .clickable(enabled = editProfileMode) {
                                if (editProfileMode) profileImageLauncher.launch("image/*")
                            },
                        contentScale = ContentScale.Crop
                    )

// Cuando el usuario selecciona una imagen, se sube a Imgur y se actualiza el estado
                    selectedProfileImageUri?.let { uri ->
                        uploadImageToImgurComposable(uri) { imgurUrl ->
                            if (imgurUrl != null) editProfileImg = TextFieldValue(imgurUrl)
                            uploadingProfileImage = false
                            selectedProfileImageUri = null
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        if (editProfileMode) {
                            OutlinedTextField(
                                value = editName,
                                onValueChange = { editName = it },
                                label = { Text("Nombre de usuario") }
                            )
                            OutlinedTextField(
                                value = editDesc,
                                onValueChange = { editDesc = it },
                                label = { Text("Descripción") }
                            )
                            Button(
                                onClick = {
                                    val db = FirebaseFirestore.getInstance()
                                    db.collection("users").document(state.userId)
                                        .update(
                                            mapOf(
                                                "name" to editName.text,
                                                "description" to editDesc.text,
                                                "profileImage" to editProfileImg.text,
                                                "bannerImage" to editBannerImg.text
                                            )
                                        )
                                    editProfileMode = false
                                    viewModel.loadUserProfile(userId, currentUserId)
                                },
                                modifier = Modifier.padding(top = 8.dp),
                                enabled = !uploadingProfileImage && !uploadingBannerImage
                            ) { Text("Guardar") }
                        } else {
                            Text(text = state.username, fontWeight = FontWeight.Bold)
                            Text(text = state.description, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    if (state.isOwnProfile) {
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(Icons.Default.Settings, contentDescription = "Ajustes")
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Añadir/Editar mascotas") },
                                    onClick = {
                                        showMenu = false
                                        navController.navigate("PetsScreen/$userId")
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Editar perfil") },
                                    onClick = {
                                        showMenu = false
                                        editProfileMode = true
                                        editName = TextFieldValue(state.username)
                                        editDesc = TextFieldValue(state.description)
                                        editProfileImg = TextFieldValue(state.profileImageUrl)
                                        editBannerImg = TextFieldValue(state.bannerUrl)
                                    }
                                )

                                DropdownMenuItem(
                                    text = { Text("Cerrar sesión") },
                                    onClick = {
                                        showMenu = false
                                        FirebaseAuth.getInstance().signOut()
                                        navController.navigate("LoginScreen") {
                                            popUpTo(0) { inclusive = true }
                                            launchSingleTop = true
                                        }
                                    }
                                )
                            }
                        }
                    } else {
                        Button(
                            onClick = { viewModel.toggleFollow(currentUserId) },
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(if (state.isFollowing) "Siguiendo" else "Seguir")
                        }
                    }
                }
            }
            // Métricas
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MetricLabel("Notas", state.notesCount)
                    ClickableTextLabel("Seguidores", state.followersCount) {
                        navController.navigate("FollowingScreen/${state.userId}?isFollow=false")
                    }
                    ClickableTextLabel("Seguidos", state.followingCount) {
                        navController.navigate("FollowingScreen/${state.userId}?isFollow=true")
                    }
                }
            }
            // Tabs
            item {
                TabRow(selectedTabIndex = selectedTab, containerColor = Color(0xFF500F24)) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        selectedContentColor = Color.White,
                        unselectedContentColor = Color.White.copy(alpha = 0.4f)
                    ) {
                        Text("General", modifier = Modifier.padding(8.dp))
                    }
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        selectedContentColor = Color.White,
                        unselectedContentColor = Color.White.copy(alpha = 0.4f)
                    ) {
                        Text("Likes", modifier = Modifier.padding(8.dp))
                    }
                }
            }
            // Sección General
            if (selectedTab == 0) {

                // Título mascotas

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Tus mascotas",
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFACC093), RoundedCornerShape(16.dp))
                            .padding(8.dp),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
                // Mascotas
                if (state.pets.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF6B122B), RoundedCornerShape(16.dp))
                                .padding(16.dp)
                                .clickable { navController.navigate("PetsScreen/$userId") },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Pets, contentDescription = null, tint = Color.White)
                                Text(
                                    text = "Añade a una mascota",
                                    color = Color.White
                                )
                            }
                        }
                    }
                } else {
                    item {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalArrangement = if (state.pets.size == 1) Arrangement.Center else Arrangement.spacedBy(24.dp)
                        ) {
                            items(state.pets) { pet ->
                                PetCard(pet = pet, navController = navController)
                            }
                        }
                    }
                }
                // Título entradas
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Entradas",
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFACC093), RoundedCornerShape(16.dp))
                            .padding(8.dp),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
                // Notas del usuario
                items(state.posts) { post ->
                    NoteCard(
                        noteId = post.noteId,
                        userName = state.username,
                        userProfileImage = state.profileImageUrl,
                        noteImages = post.images,
                        noteDate = post.date.let {
                            when (it) {
                                is Timestamp -> dateFormatter.format(it.toDate())
                                is String -> it
                                else -> "Fecha desconocida"
                            }
                        },
                        noteTitle = post.title,
                        noteContent = post.content,
                        likeCount = (post.likes as? List<*>)?.size ?: 0,
                        commentCount = (post.comments as? List<*>)?.size ?: 0,
                        isLiked = false,
                        userIdActual = userId,
                        noteOwnerId = state.userId,
                        navController = navController,
                        noteViewModel = noteViewModel
                    )
                }
            }
            // Sección Likes
            if (selectedTab == 1) {
                item {
                    TextTitleWithBackgroundRound(sectionTitle = "Notas que te han gustado")
                }
                items(state.likedPosts) { post ->
                    NoteCardWithUserData(
                        noteId = post.noteId.substringAfterLast("/"),
                        noteImages = post.images,
                        noteDate = when (val date = post.date) {
                            is Timestamp -> dateFormatter.format(date.toDate())
                            is String -> date
                            else -> "Fecha desconocida"
                        },
                        noteTitle = post.title,
                        noteContent = post.content,
                        likeCount = post.likes.size,
                        commentCount = post.comments.size,
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
