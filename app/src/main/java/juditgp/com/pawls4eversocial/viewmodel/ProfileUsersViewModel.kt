package juditgp.com.pawls4eversocial.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import juditgp.com.pawls4eversocial.model.UserProfileState
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import juditgp.com.pawls4eversocial.model.Note
import juditgp.com.pawls4eversocial.model.Pet
import juditgp.com.pawls4eversocial.model.User

class ProfileUsersViewModel : ViewModel() {
    var state by mutableStateOf(UserProfileState())
        private set

    private val db = FirebaseFirestore.getInstance()

    fun loadUserProfile(userId: String, currentUserId: String) {
        viewModelScope.launch {
            db.collection("users").document(userId).get().addOnSuccessListener { document ->
                val user = document.toObject<User>()
                if (user != null) {
                    loadUserNotes(user.userNotes) { notes ->
                        loadUserNotes(user.likesGiven) { likedNotes ->
                            loadUserPets(user.pets) { pets ->
                                state = state.copy(
                                    userId = user.userId,
                                    username = user.name ?: "",
                                    email = user.email,
                                    description = user.description,
                                    profileImageUrl = user.profileImage ?: "",
                                    bannerUrl = user.bannerImage.toString(),
                                    notesCount = notes.size,
                                    followersCount = user.followers.size,
                                    followingCount = user.following.size,
                                    isOwnProfile = userId == currentUserId,
                                    isFollowing = user.followers.any { it.id == currentUserId },
                                    pets = pets,
                                    posts = notes,
                                    likedPosts = likedNotes
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // En ProfileUsersViewModel.kt
    private fun loadUserNotes(noteRefs: List<DocumentReference>, callback: (List<Note>) -> Unit) {
        if (noteRefs.isEmpty()) {
            callback(emptyList())
            return
        }

        val notes = mutableListOf<Note>()
        for (ref in noteRefs) {
            ref.get().addOnSuccessListener { document ->
                val note = document.toObject<Note>()
                if (note != null && note.private == false) { // Solo públicas
                    notes.add(note)
                }
                if (notes.size + noteRefs.size - notes.size == noteRefs.size) {
                    callback(notes)
                }
            }
        }
    }

    private fun loadUserPets(petRefs: List<com.google.firebase.firestore.DocumentReference>, callback: (List<Pet>) -> Unit) {
        if (petRefs.isEmpty()) {
            callback(emptyList())
            return
        }
        val pets = mutableListOf<Pet>()
        for (ref in petRefs) {
            ref.get().addOnSuccessListener { document ->
                val pet = document.toObject<Pet>()
                if (pet != null) {
                    pets.add(pet)
                }
                if (pets.size == petRefs.size) {
                    callback(pets)
                }
            }
        }
    }

    fun toggleFollow(currentUserId: String) {
        viewModelScope.launch {
            val userRef = db.collection("users").document(state.userId) // usuario visitado
            val currentUserRef = db.collection("users").document(currentUserId) // usuario actual

            if (state.isFollowing) {
                userRef.update("followers", com.google.firebase.firestore.FieldValue.arrayRemove(currentUserRef))
                currentUserRef.update("following", com.google.firebase.firestore.FieldValue.arrayRemove(userRef))
            } else {
                userRef.update("followers", com.google.firebase.firestore.FieldValue.arrayUnion(currentUserRef))
                currentUserRef.update("following", com.google.firebase.firestore.FieldValue.arrayUnion(userRef))

                // Crear notificación de seguimiento
                val notificationsRef = db.collection("notifications")
                val notificationId = notificationsRef.document().id
                val notificationData = mapOf(
                    "idNotification" to notificationId,
                    "idUserProvider" to currentUserId,
                    "idUserReceptor" to state.userId,
                    "content" to "¡${state.username} te ha seguido!",
                    "seen" to false,
                    "idNoteRelated" to "" // o null si no aplica
                )
                notificationsRef.document(notificationId).set(notificationData)
            }
            // Recargar el estado tras actualizar
            loadUserProfile(state.userId, currentUserId)
        }
    }
}
