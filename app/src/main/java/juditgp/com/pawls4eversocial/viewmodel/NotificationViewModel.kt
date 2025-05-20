package juditgp.com.pawls4eversocial.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import juditgp.com.pawls4eversocial.model.Notification
import kotlin.text.get

class NotificationViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount



    fun loadNotifications(userId: String) {
        if (userId.isBlank()) return // Evita referencias inválidas
        db.collection("notifications")
            .whereEqualTo("userReceptor", userId) // userId como string
            .get()
            .addOnSuccessListener { documents ->
                val notificationsList = documents.mapNotNull { it.toObject<Notification>() }
                _notifications.value = notificationsList
                _unreadCount.value = notificationsList.count { !it.seen }
            }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            val batch = db.batch()
            _notifications.value.forEach { notification ->
                if (!notification.seen) {
                    val notificationRef = db.collection("notifications").document(notification.notificationId)
                    batch.update(notificationRef, "seen", true)
                }
            }
            batch.commit().addOnSuccessListener {
                _notifications.value = _notifications.value.map { it.copy(seen = true) }
                _unreadCount.value = 0
            }
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            val notificationRef = db.collection("notifications").document(notificationId)
            notificationRef.update("seen", true).addOnSuccessListener {
                _notifications.value = _notifications.value.map { notification ->
                    if (notification.notificationId == notificationId) {
                        notification.copy(seen = true)
                    } else {
                        notification
                    }
                }
                _unreadCount.value = _notifications.value.count { !it.seen }
            }
        }
    }
}