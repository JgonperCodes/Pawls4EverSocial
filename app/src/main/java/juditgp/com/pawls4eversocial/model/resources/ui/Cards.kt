package juditgp.com.pawls4eversocial.model.resources.ui

import android.media.MediaPlayer
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Share
import juditgp.com.pawls4eversocial.R
import androidx.compose.material3.Icon
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import com.example.pawls4ever.navigation.Screens
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import juditgp.com.pawls4eversocial.model.Pet
import juditgp.com.pawls4eversocial.ui.theme.Roboto
import juditgp.com.pawls4eversocial.viewmodel.NoteViewModel
import kotlinx.coroutines.tasks.await
import kotlin.text.get


@Composable
fun NoteCard(
    noteId: String,
    userName: String,
    userProfileImage: String,
    noteImages: List<String>,
    noteDate: String,
    noteTitle: String,
    noteContent: String,
    likeCount: Int,
    commentCount: Int,
    isLiked: Boolean,
    userIdActual: String,
    noteOwnerId: String,
    navController: NavController,
    noteViewModel: NoteViewModel
) {
    val likeState = remember { mutableStateOf(isLiked) }
    val scale = remember { Animatable(1f) }
    val context = LocalContext.current
    val mediaPlayer = remember { MediaPlayer.create(context, R.raw.barksound) }
    val likeCountState = remember { mutableStateOf(likeCount) }

    LaunchedEffect(noteId) {
        noteViewModel.observeLikes(noteId) { newCount ->
            likeCountState.value = newCount
        }
    }
    LaunchedEffect(likeState.value) {
        if (likeState.value) {
            scale.animateTo(1.2f, animationSpec = tween(100))
            scale.animateTo(1f, animationSpec = tween(100))
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = rememberImagePainter(data = userProfileImage),
                    contentDescription = "Imagen de perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Gray, shape = CircleShape)
                        .clickable {
                            navController.navigate("ProfileUserScreen/$noteOwnerId")
                        }
                )
                Column(modifier = Modifier.padding(start = 8.dp)) {
                    Text(text = userName, fontWeight = FontWeight.Bold)
                    Text(text = noteDate, fontSize = 12.sp, color = Color.Gray)
                    Text( // Nuevo texto para el título de la nota
                        text = noteTitle,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                }
            }

            val noteImage = noteImages.firstOrNull()
            if (noteImage != null) {
                Image(
                    painter = rememberImagePainter(data = noteImage),
                    contentDescription = "Imagen de la nota",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .padding(vertical = 8.dp)
                )
            } else {
                Text(
                    text = noteContent, // <-- Ahora muestra el contenido real
                    fontSize = 16.sp,
                    color = Color.DarkGray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Reblog
                IconButton(onClick = {
                    noteViewModel.shareNote(noteId, context) { /* callback opcional */ }
                }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Reblog",
                        tint = Color.Gray
                    )
                }

                // Comentario
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {
                        navController.navigate("NewMemoryScreen/$userIdActual?isViewing=true&noteId=$noteId")
                    }) {
                        Icon(
                            imageVector = Icons.Default.Comment,
                            contentDescription = "Comentario",
                            tint = Color.Gray
                        )
                    }
                    Text(text = commentCount.toString(), fontSize = 12.sp, color = Color.Gray)
                }

                // Likes
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            likeState.value = !likeState.value
                            noteViewModel.toggleLike(
                                noteId = noteId,
                                userIdReceptor = noteOwnerId,
                                isLiked = likeState.value
                            )
                            if (likeState.value) {
                                mediaPlayer.start()
                            }
                        },
                        modifier = Modifier.scale(scale.value)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.icononarizperro),
                            contentDescription = "Like",
                            tint = if (likeState.value) Color.Red else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(text = likeCountState.value.toString(), fontSize = 12.sp, color = Color.Gray)
                }
            }
        }

    }
}

@Composable
fun NoteCardWithUserData(
    noteId: String,
    noteImages: List<String>,
    noteDate: String,
    noteTitle: String,
    noteContent: String,
    likeCount: Int,
    commentCount: Int,
    isLiked: Boolean,
    userIdActual: String,
    navController: NavController,
    noteViewModel: NoteViewModel
) {
    var userName by remember { mutableStateOf("Usuario desconocido") }
    var userProfileImage by remember { mutableStateOf("https://imgur.com/defaultProfileImage.jpg") }
    var noteOwnerId by remember { mutableStateOf("") }

    LaunchedEffect(noteId) {
        val db = FirebaseFirestore.getInstance()
        val noteRef = db.collection("notes").document(noteId)
        val usersQuery = db.collection("users")
            .whereArrayContains("userNotes", noteRef)
            .limit(1)
            .get()
            .await()
        val userDoc = usersQuery.documents.firstOrNull()
        if (userDoc != null) {
            userName = userDoc.getString("name") ?: "Usuario desconocido"
            userProfileImage = userDoc.getString("profileImage") ?: "https://imgur.com/defaultProfileImage.jpg"
            noteOwnerId = userDoc.getString("userId") ?: ""
        }
    }

    NoteCard(
        noteId = noteId,
        userName = userName,
        userProfileImage = userProfileImage,
        noteImages = noteImages,
        noteDate = noteDate,
        noteTitle = noteTitle,
        noteContent = noteContent,
        likeCount = likeCount,
        commentCount = commentCount,
        isLiked = isLiked,
        userIdActual = userIdActual,
        noteOwnerId = noteOwnerId, // <- Aquí pasas el id correcto
        navController = navController,
        noteViewModel = noteViewModel
    )
}

//Funcion para dar formato a las card de pets
@Composable
fun PetCard(pet: Pet, navController: NavController) {
    Card(
        modifier = Modifier
            .width(145.dp)
            .height(180.dp)
            .clickable { //Para que te lleve al pulsar a la pantalla de mascotas
                val currentUser = FirebaseAuth.getInstance().currentUser
                val userId = currentUser?.uid ?: ""
                navController.navigate("${Screens.PetsScreen.name}/$userId")
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(8.dp)
            ) {
                // Imagen de mascota
                Image(
                    painter = rememberImagePainter(
                        data = pet.image ?: "https://i.imgur.com/3jqQjz5.jpeg"
                    ),
                    contentDescription = "Pet Image",
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Nombre y detalles
                Text(
                    text = pet.name,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                )
                Text(
                    text = pet.breed,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp
                    )
                )
                Text(
                    text = "${pet.age} años",
                    color = Color.Gray,
                    style = TextStyle(
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Normal,
                    )
                )
            }
        }
    }
}