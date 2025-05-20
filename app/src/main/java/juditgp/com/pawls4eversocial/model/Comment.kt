package juditgp.com.pawls4eversocial.model

import com.google.firebase.firestore.DocumentReference

data class Comment(
    val idComment: String = "",
    val userReceptor: DocumentReference? = null,
    val userProvider: DocumentReference? = null,
    val noteRelated: DocumentReference? = null,
    val content: String
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "idComment" to this.idComment,
            "userProvider" to this.userProvider,
            "userReceptor" to this.userReceptor,
            "noteRelated" to this.noteRelated,
            "content" to this.content
        )
    }
}