package juditgp.com.pawls4eversocial.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore



/** ViewModel para manejar la lógica de la pantalla de registro.
 *
 * @property email Correo electrónico del usuario.
 * @property name Nombre de usuario.
 * @property password Contraseña del usuario.
 * @property showToast Bandera para mostrar un mensaje emergente (toast).
 * @property toastMessage Mensaje del toast.
 * @property isPositiveToast Bandera para indicar si el toast es positivo o negativo.
 */


/* Constantes privadas de la clase para manejar los
* mensajes de error y mantener facilmente la aplicación*/
private const val CORRECT_REGISTATION = "Usuario registrado con éxito."
private const val ERROR_EMAIL_USED = "El correo electrónico ya está en uso."
private const val ERROR_USERNAME_USED = "El nombre de usuario ya está en uso."
private const val ERROR_EMAIL_VALID = "El correo electrónico debe ser un Gmail válido."
private const val ERROR_EMAIL_UNKNOWN = "Error al verificar el correo electrónico."
private const val ERROR_USERNAME_UNKNOWN = "Error al verificar el nombre de usuario."
private const val ERROR_PASSWORD_LENGTH = "La contraseña debe tener más de 6 caracteres."
private const val ERROR_NULL_FIELDS = "Todos los campos son obligatorios."
private const val ERROR_REGISTRATION = "Error al registrar el usuario."
private const val ERROR_UNKNOWN = "Error desconocido."
private const val LINK_PROFILE = "https://i.imgur.com/3r9OBxg.jpeg"
private const val LINK_BANNER = "https://i.imgur.com/3pXnBWG.jpeg"

class SignUpViewModel: ViewModel() {
    val email = mutableStateOf("")
    val name = mutableStateOf("")
    val password = mutableStateOf("")
    val showToast = mutableStateOf(false)
    val toastMessage = mutableStateOf("")
    val isPositiveToast = mutableStateOf(false)
    val showVerifyDialog = mutableStateOf(false)
    val isVerifying = mutableStateOf(false)

    fun singUpUser(onSuccess: () -> Unit) {
        if (checkNullFields()) return
        if (checkPasswordLenght()) return
        checkUserNameIsValid {
            checkEmailIsValid {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.value, password.value)
                    .addOnSuccessListener { authResult ->
                        val uid = authResult.user?.uid ?: return@addOnSuccessListener
                        val userData = mapOf(
                            "userId" to uid,
                            "email" to email.value,
                            "name" to name.value,
                            "profileImage" to LINK_PROFILE,
                            "bannerImage" to LINK_BANNER,
                            "following" to emptyList<String>(),
                            "pets" to emptyList<String>(),
                            "followers" to emptyList<String>(),
                            "description" to "",
                            "likesGiven" to emptyList<String>(),
                            "userNotes" to emptyList<String>()
                        )
                        FirebaseFirestore.getInstance().collection("users").document(uid).set(userData)
                            .addOnSuccessListener {
                                authResult.user?.sendEmailVerification()
                                showVerifyDialog.value = true
                                isVerifying.value = true
                            }
                            .addOnFailureListener { /* Manejo de error */ }
                    }
                    .addOnFailureListener { /* Manejo de error */ }
            }
        }
    }
    // Función para registrar un nuevo usuario en la base de datos
//    fun singUpUser(onSuccess: () -> Unit) {
//        if (checkNullFields()) return
//        if (checkPasswordLenght()) return
//        checkUserNameIsValid {
//            checkEmailIsValid {
//                // Si pasa todas las validaciones, se registra el usuario
//                registerUserInDatabaseAndAuth(
//                    email = email.value,
//                    name = name.value,
//                    password = password.value,
//                    onSuccess = {
//                        toastMessage.value = CORRECT_REGISTATION
//                        isPositiveToast.value = true
//                        showToast.value = true
//                        // Aquí NO navegues, solo muestra el diálogo
//                        // showVerifyDialog ya se activa en registerUserInDatabaseAndAuth
//                    },
//                    onFailure = { errorMessage ->
//                        toastMessage.value = errorMessage
//                        isPositiveToast.value = false
//                        showToast.value = true
//                    }
//                )
//            }
//        }
//    }
    fun checkEmailVerified(onVerified: () -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        user?.reload()?.addOnSuccessListener {
            if (user.isEmailVerified) {
                isVerifying.value = false
                showVerifyDialog.value = false
                onVerified()
            }
        }
    }
    // Verifica si el correo electrónico ya existe en la base de datos y si es un Gmail válido
    private fun checkEmailIsValid(onSuccess: () -> Unit) {
        if (!email.value.endsWith("@gmail.com")) {
            toastMessage.value = ERROR_EMAIL_VALID
            isPositiveToast.value = false
            showToast.value = true
            return
        }

        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("users")
            .whereEqualTo("email", email.value)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    toastMessage.value = ERROR_EMAIL_USED
                    isPositiveToast.value = false
                    showToast.value = true
                } else {
                    onSuccess() // <- Importante
                }
            }
            .addOnFailureListener {
                toastMessage.value = ERROR_EMAIL_UNKNOWN
                isPositiveToast.value = false
                showToast.value = true
            }
    }
    // Verifica si el nombre de usuario ya existe en la base de datos
    private fun checkUserNameIsValid(onSuccess: () -> Unit) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("users")
            .whereEqualTo("name", name.value)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    toastMessage.value = ERROR_USERNAME_USED
                    isPositiveToast.value = false
                    showToast.value = true
                } else {
                    onSuccess()
                }
            }
            .addOnFailureListener {
                toastMessage.value = ERROR_USERNAME_UNKNOWN
                isPositiveToast.value = false
                showToast.value = true
            }
    }

    // Verifica si la contraseña tiene más de 6 caracteres
    private fun checkPasswordLenght(): Boolean {
        if (password.value.length <= 6) {
            toastMessage.value = ERROR_PASSWORD_LENGTH
            isPositiveToast.value = false
            showToast.value = true
            return true
        }
        return false
    }

    // Verifica si hay campos vacíos
    private fun checkNullFields(): Boolean {
        if (email.value.isBlank() || password.value.isBlank() || name.value.isBlank()) {
            toastMessage.value = ERROR_NULL_FIELDS
            isPositiveToast.value = false
            showToast.value = true
            return true
        }
        return false
    }

    fun dismissToast() {
        showToast.value = false
    }

    fun registerUserInDatabaseAndAuth(
        email: String,
        name: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val firestore = FirebaseFirestore.getInstance()
        val defaultProfileImage = LINK_PROFILE
        val defaultBannerImage = LINK_BANNER
        val userData = mapOf(
            "email" to email,
            "name" to name,
            "profileImage" to defaultProfileImage,
            "bannerImage" to defaultBannerImage,
            "following" to emptyList<String>(),
            "pets" to emptyList<String>(),
            "followers" to emptyList<String>(),
            "description" to "",
            "likesGiven" to emptyList<String>(),
            "userNotes" to emptyList<String>()
        )

        firestore.collection("users")
            .add(userData)
            .addOnSuccessListener { documentReference ->
                val userId = documentReference.id
                documentReference.update("userId", userId)
                    .addOnSuccessListener {
                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                            .addOnSuccessListener { authResult ->
                                authResult.user?.sendEmailVerification()
                                showVerifyDialog.value = true
                                isVerifying.value = true
                                // No llamar a onSuccess aún, esperar verificación
                            }
                            .addOnFailureListener { exception ->
                                toastMessage.value = ERROR_REGISTRATION + " " + exception.message
                                isPositiveToast.value = false
                                showToast.value = true
                                onFailure(exception.message ?: ERROR_UNKNOWN)
                            }
                    }
                    .addOnFailureListener { exception ->
                        toastMessage.value = ERROR_REGISTRATION
                        isPositiveToast.value = false
                        showToast.value = true
                        onFailure(exception.message ?: ERROR_UNKNOWN)
                    }
            }
            .addOnFailureListener { exception ->
                toastMessage.value = ERROR_REGISTRATION
                isPositiveToast.value = false
                showToast.value = true
                onFailure(exception.message ?: ERROR_UNKNOWN)
            }
    }
}