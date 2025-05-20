package juditgp.com.pawls4eversocial.model

import com.google.firebase.firestore.DocumentReference


data class Notification(
    val notificationId: String = "",
    val content: String = "",
    val idLike: DocumentReference? = null,
    val idComment: DocumentReference? = null,
    val seen: Boolean = false,
    val userProvider: DocumentReference? = null,
    val userReceptor: DocumentReference? = null,
    val noteRelated: DocumentReference? = null,
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "notificationId" to this.notificationId,
            "content" to this.content,
            "idLike" to this.idLike,
            "idComment" to this.idComment,
            "seen" to this.seen,
            "userProvider" to this.userProvider,
            "userReceptor" to this.userReceptor,
            "noteRelated" to this.noteRelated
        )
    }
}
