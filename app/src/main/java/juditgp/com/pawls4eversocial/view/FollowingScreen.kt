package juditgp.com.pawls4eversocial.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.navigation.NavController
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import juditgp.com.pawls4eversocial.model.User
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.draw.clip
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import com.example.pawls4ever.navigation.BottomNavigationBar
import androidx.compose.material3.Scaffold


@Composable
fun FollowingScreen(navController: NavController, userId: String, isFollow: Boolean) {
    val firestore = Firebase.firestore
    val users = remember { mutableStateListOf<User>() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val userDoc = firestore.collection("users").document(userId).get().await()
        val listRef = if (isFollow) userDoc["following"] else userDoc["followers"]
        val userRefs = listRef as? List<DocumentReference> ?: emptyList()

        users.clear()
        userRefs.forEach { ref ->
            scope.launch {
                val userSnapshot = ref.get().await()
                val user = userSnapshot.toObject(User::class.java)?.copy(userId = userSnapshot.id)
                user?.let {
                    users.add(it)
                }
            }
        }
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFDF1E7))
                .padding(start = 8.dp, end = 8.dp, top = 10.dp)
                .padding(innerPadding)
        ) {
            Text(
                text = if (isFollow) "Seguidos" else "Seguidores",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF7C956B), RoundedCornerShape(20.dp))
                    .padding(vertical = 12.dp),
                textAlign = TextAlign.Center
            )

            if (users.isEmpty()) {
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "¡Prueba a seguir utilizando la aplicación y tener posts públicos!",
                    color = Color.Gray,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    textAlign = TextAlign.Center
                )
            } else {
                LazyColumn {
                    items(users) { user ->
                        ProfileUserCard(user, navController)
                    }
                }
            }
        }
    }
}
@Composable
fun ProfileUserCard(user: User, navController: NavController) {
    val firestore = Firebase.firestore
    var petImages by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }

    // Cargar imágenes y nombres de mascotas
    LaunchedEffect(user.userId) {
        val petRefs = user.pets ?: emptyList()
        val petImgs = mutableListOf<Pair<String, String>>()
        petRefs.forEach { ref ->
            val petSnap = ref.get().await()
            val image = petSnap.getString("image")
            val name = petSnap.getString("name") ?: ""
            if (image != null) {
                petImgs.add(image to name)
            }
        }
        petImages = petImgs
    }

    val hasPets = petImages.isNotEmpty()

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { navController.navigate("ProfileUserScreen/${user.userId}") }
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start // Alineado a la izquierda
        ) {
            // Foto de perfil y nombre alineados a la izquierda
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                AsyncImage(
                    model = user.profileImage,
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(if (hasPets) 80.dp else 80.dp) // Más grande si tiene mascotas
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = user.name ?: "Usuario",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
            }

            // Mascotas debajo del usuario
            if (hasPets) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    petImages.take(3).forEach { (img, name) ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(end = 16.dp)
                        ) {
                            AsyncImage(
                                model = img,
                                contentDescription = "Pet Image",
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(18.dp)), // Cuadrado con bordes redondeados
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = name,
                                fontSize = 14.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1
                            )
                        }
                    }
                }
                if (petImages.size == 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Este usuario no ha registrado otras mascotas.",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                }
            }
        }
    }
}