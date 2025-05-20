package juditgp.com.pawls4eversocial.model
import com.google.firebase.firestore.DocumentReference
import java.util.Date

data class Note(
    val noteId: String = "",
    val content: String = "",
    val date: Date? = null,
    val images: List<String> = emptyList(),
    val title: String = "",
    val favorite: Boolean = false,
    val happyMemory: Int = 0,
    val petTag: List<DocumentReference> = emptyList(),
    val likes: List<DocumentReference> = emptyList(),
    val comments: List<DocumentReference> = emptyList(),
    val private: Boolean = false
) {
    fun toMap(): MutableMap<String, Any?> {
        return mutableMapOf(
            "noteId" to this.noteId,
            "content" to this.content,
            "date" to this.date,
            "images" to this.images,
            "title" to this.title,
            "favorite" to this.favorite,
            "happyMemory" to this.happyMemory,
            "petTag" to this.petTag,
            "likes" to this.likes,
            "comments" to this.comments,
            "private" to this.private
        )
    }
}