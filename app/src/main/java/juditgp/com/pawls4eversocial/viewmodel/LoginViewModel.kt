package juditgp.com.pawls4eversocial.viewmodel

import android.util.Patterns
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginViewModel : ViewModel() {
    val email = mutableStateOf("")
    val password = mutableStateOf("")
    val userName = mutableStateOf("")
    val showToast = mutableStateOf(false)
    val toastMessage = mutableStateOf("")
    val isPositiveToast = mutableStateOf(false)

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun verifyUser(onSuccess: () -> Unit) {
        if (email.value.isEmpty()) {
            setToastMessage("Por favor, introduce tu Gmail.", false)
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.value).matches()) {
            setToastMessage("Por favor, introduce un Gmail válido.", false)
        } else {
            auth.fetchSignInMethodsForEmail(email.value)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val signInMethods = task.result?.signInMethods
                        if (signInMethods.isNullOrEmpty()) {
                            setToastMessage("El usuario no está registrado.", false)
                        } else {
                            setToastMessage("Se ha enviado un correo para recuperar la contraseña.", true)
                            onSuccess()
                        }
                    } else {
                        setToastMessage("Error: ${task.exception?.message}", false)
                    }
                }
        }
    }

    fun loginUser(onSuccess: () -> Unit) {
        if (email.value.isEmpty()) {
            setToastMessage("Por favor, introduce tu correo electrónico.", false)
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.value).matches()) {
            setToastMessage("Por favor, introduce un correo válido.", false)
            return
        }
        if (password.value.isEmpty()) {
            setToastMessage("Por favor, introduce tu contraseña.", false)
            return
        }

        auth.signInWithEmailAndPassword(email.value, password.value)
            .addOnSuccessListener {
                setToastMessage("Inicio de sesión exitoso.", true)
                onSuccess()
            }
            .addOnFailureListener {
                setToastMessage("Error: ${it.message}", false)
            }
    }

    private fun setToastMessage(message: String, isPositive: Boolean) {
        toastMessage.value = message
        isPositiveToast.value = isPositive
        showToast.value = true
    }

    fun dismissToast() {
        showToast.value = false
    }
}