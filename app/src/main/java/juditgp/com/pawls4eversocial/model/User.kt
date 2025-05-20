package juditgp.com.pawls4eversocial.model

import com.google.firebase.firestore.DocumentReference

data class User(
    val userId: String = "",
    val email: String = "",
    val name: String? = "",
    val profileImage: String? = "",
    val bannerImage: String= "",
    val following: List<DocumentReference> = emptyList(),
    val pets: List<DocumentReference> = emptyList(),
    val followers: List<DocumentReference> = emptyList(),
    val description: String = "",
    val likesGiven : List<DocumentReference> = emptyList(),
    val userNotes : List<DocumentReference> = emptyList()
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "userId" to this.userId,
            "email" to this.email,
            "name" to this.name,
            "profileImage" to this.profileImage,
            "bannerImage" to this.bannerImage,
            "following" to this.following,
            "pets" to this.pets,
            "followers" to this.followers,
            "description" to this.description,
            "likesGiven" to this.likesGiven,
            "userNotes" to this.userNotes
        )
    }
}
