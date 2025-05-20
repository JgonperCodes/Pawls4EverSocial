package juditgp.com.pawls4eversocial.model

import com.google.firebase.firestore.DocumentReference

data class Like(
    val idLike: String = "",
    val userReceptor: DocumentReference? = null,
    val userProvider: DocumentReference? = null,
    val noteRelated: DocumentReference? = null
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "idLike" to this.idLike,
            "userProvider" to this.userProvider,
            "userReceptor" to this.userReceptor,
            "noteRelated" to this.noteRelated
        )
    }
}
