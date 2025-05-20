package juditgp.com.pawls4eversocial.model.resources.functions

import android.annotation.SuppressLint
import juditgp.com.pawls4eversocial.R
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pawls4ever.navigation.Screens
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import juditgp.com.pawls4eversocial.ui.theme.Roboto
import juditgp.com.pawls4eversocial.ui.theme.White

/** Constantes privadas del archivo para manejar los
 * mensajes de error y mantener facilmente la aplicación*/
private const val LINK_PROFILE = "https://i.imgur.com/3r9OBxg.jpeg"
private const val LINK_BANNER = "https://i.imgur.com/3pXnBWG.jpeg"

/**
 * Función para manejar el inicio de sesión con Google.
 *
 * @param context Contexto de la aplicación.
 * @param navController Controlador de navegación para navegar entre pantallas.
 */
@SuppressLint("SuspiciousIndentation")
@Composable
fun GoogleLogin(context: Context, navController: NavController) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnSuccessListener { authResult ->
                        val db = FirebaseFirestore.getInstance()
                        val uid = authResult.user?.uid ?: ""
                        if (uid.isEmpty()) {
                            Toast.makeText(context, "Error: UID de usuario no válido.", Toast.LENGTH_LONG).show()
                            return@addOnSuccessListener
                        }
                        db.collection("users").document(uid).get().addOnSuccessListener { document ->
                            if (document.exists()) {

                                saveUserIdToPreferences(context, uid)
                                navController.navigate(Screens.HomeScreen.name) {
                                    popUpTo(Screens.LoginScreen.name) { inclusive = true }
                                }
                            } else {
                                Toast.makeText(context, "La cuenta no está registrada.", Toast.LENGTH_LONG).show()
                            }
                        }.addOnFailureListener {
                            Toast.makeText(context, "Error al verificar usuario: ${it.message}", Toast.LENGTH_LONG).show()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(context, "Error al autenticar con Google: ${it.message}", Toast.LENGTH_LONG).show()
                    }

            } catch (e: ApiException) {
                Log.e("GSIGNIN", "Google Sign-In failed, code=${e.statusCode}", e)
                Toast.makeText(context, "Error Google Sign In: code=${e.statusCode}", Toast.LENGTH_LONG).show()
            }
        }
    )
    GoogleButton(
        text = "Login con Google",
        onClick = {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("510018513337-2798p2vo8es4e4v7ou3a46b341r2n43h.apps.googleusercontent.com")
                .requestEmail()
                .build()


            val googleSignInClient = GoogleSignIn.getClient(context, gso)
            launcher.launch(googleSignInClient.signInIntent)
        }
    )
}

/**
 * Función para manejar el registro de una cuenta con Google.
 *
 * @param context Contexto de la aplicación.
 * @param navController Controlador de navegación para navegar entre pantallas.
 */
@SuppressLint("SuspiciousIndentation")
@Composable
fun GoogleRegisterAccount(context: Context, navController: NavController) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnSuccessListener { authResult ->
                        val db = FirebaseFirestore.getInstance()
                        val uid = authResult.user?.uid ?: ""
                        val email = authResult.user?.email ?: ""
                        val baseUsername = email.substringBefore("@")

                        // URLs genéricas para las imágenes de perfil y banner
                        val defaultProfileImage = LINK_PROFILE
                        val defaultBannerImage = LINK_BANNER

                        // Generar un nombre de usuario único
                        fun generateUniqueUsername(callback: (String) -> Unit) {
                            val randomSuffix = (1000..9999).random()
                            val potentialUsername = "$baseUsername$randomSuffix"
                            db.collection("users")
                                .whereEqualTo("name", potentialUsername)
                                .get()
                                .addOnSuccessListener { querySnapshot ->
                                    if (querySnapshot.isEmpty) {
                                        callback(potentialUsername)
                                    } else {
                                        generateUniqueUsername(callback) // Intenta de nuevo
                                    }
                                }
                        }

                        db.collection("users").document(uid).get().addOnSuccessListener { document ->
                            if (!document.exists()) {
                                generateUniqueUsername { uniqueUsername ->
                                    // Datos iniciales del usuario
                                    val userData = mapOf(
                                        "userId" to uid,
                                        "email" to email,
                                        "name" to uniqueUsername,
                                        "profileImage" to defaultProfileImage,
                                        "bannerImage" to defaultBannerImage,
                                        "following" to emptyList<String>(),
                                        "pets" to emptyList<String>(),
                                        "followers" to emptyList<String>(),
                                        "description" to "",
                                        "likesGiven" to emptyList<String>(),
                                        "userNotes" to emptyList<String>()
                                    )
                                    db.collection("users").document(uid).set(userData)
                                        .addOnSuccessListener {
                                            saveUserIdToPreferences(context, uid)
                                            navController.navigate(Screens.HomeScreen.name) {
                                                popUpTo(Screens.LoginScreen.name) { inclusive = true }
                                            }
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(context, "Error al registrar usuario: ${it.message}", Toast.LENGTH_LONG).show()
                                        }
                                }
                            } else {
                                navController.navigate(Screens.HomeScreen.name) {
                                    popUpTo(Screens.LoginScreen.name) { inclusive = true }
                                }
                            }
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Error al autenticar con Google: ${it.message}", Toast.LENGTH_LONG).show()
                    }
            } catch (e: ApiException) {
                Toast.makeText(context, "Error Google Register: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    )

        GoogleButton(
            text = "Login con Google",
            onClick = {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("510018513337-2798p2vo8es4e4v7ou3a46b341r2n43h.apps.googleusercontent.com")
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(context, gso)
            launcher.launch(googleSignInClient.signInIntent)
        }
        )
}

/**
 * Función para crear un botón de Google con texto personalizado.
 *
 * @param text Texto que se mostrará en el botón.
 * @param onClick Acción a realizar al hacer clic en el botón.
 * @param modifier Modificador opcional para personalizar el botón.
 * @param backgroundColor Color de fondo del botón.
 * @param textColor Color del texto del botón.
 */
@Composable
fun GoogleButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Transparent,
    textColor: Color = White
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.ic_google),
            contentDescription = "Google",
            modifier = Modifier
                .size(50.dp)
                .fillMaxWidth(0.1f)
                .padding(end = 8.dp)
        )

        Text(
            text = text,
            color = textColor,
            fontFamily = Roboto,
            fontSize = 20.sp
        )
    }
}

/**
 * Función para guardar el ID de usuario en las preferencias compartidas.
 *
 * @param context Contexto de la aplicación.
 * @param userId ID del usuario a guardar.
 */
fun saveUserIdToPreferences(context: Context, userId: String) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("userId", userId)
    editor.apply()
}