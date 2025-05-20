package juditgp.com.pawls4eversocial.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.text.get

class HomeViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _userLastPost = MutableStateFlow<Map<String, Any>?>(null)
    val userLastPost: StateFlow<Map<String, Any>?> = _userLastPost

    private val _publicPosts = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val publicPosts: StateFlow<List<Map<String, Any>>> = _publicPosts

    private val _followedPosts = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val followedPosts: StateFlow<List<Map<String, Any>>> = _followedPosts

    private val _userFollowing = MutableStateFlow<List<String>>(emptyList())
    val userFollowing: StateFlow<List<String>> = _userFollowing

    fun loadUserLastPost(userId: String) {
        if (userId.isEmpty()) return // Validación
        viewModelScope.launch {
            db.collection("notes")
                .whereEqualTo("userId", userId)
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val document = documents.first()
                        _userLastPost.value = document.data.apply {
                            put("noteId", document.id)
                        }
                    } else {
                        _userLastPost.value = null
                    }
                }
                .addOnFailureListener {
                    _userLastPost.value = null
                }
        }
    }

    fun loadUserFollowing(userId: String) {
        if (userId.isEmpty()) return // Validación
        viewModelScope.launch {
            db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    val following = document.get("following") as? List<String>
                    _userFollowing.value = following ?: emptyList()
                }
        }
    }

    // En HomeViewModel.kt
    fun getUserIdFromPreferences(context: Context): String {
        val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("userId", "") ?: ""
    }

    fun loadFollowedPosts() {
        if (_userFollowing.value.isNotEmpty()) {
            viewModelScope.launch {
                db.collection("notes")
                    .whereIn("userId", _userFollowing.value)
                    .whereEqualTo("private", false)
                    .orderBy("date", Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener { documents ->
                        _followedPosts.value = documents.map { doc ->
                            doc.data.toMutableMap().apply {
                                put("userId", doc.getString("userId") ?: "")
                                put("noteId", doc.id)
                            }
                        }
                    }
            }
        }
    }

    fun loadPublicPosts() {
        viewModelScope.launch {
            db.collection("notes")
                .whereEqualTo("private", false)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { documents ->
                    _publicPosts.value = documents.map { doc ->
                        doc.data.toMutableMap().apply {
                            put("userId", doc.getString("userId") ?: "")
                            put("noteId", doc.id)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                }
        }
    }
}
