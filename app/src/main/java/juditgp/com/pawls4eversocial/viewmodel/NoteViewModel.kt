package juditgp.com.pawls4eversocial.viewmodel

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks

class NoteViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun observeLikes(noteId: String, onLikesChanged: (Int) -> Unit) {
        db.collection("likes")
            .whereEqualTo("idNoteRelated", noteId)
            .addSnapshotListener { snapshot, _ ->
                onLikesChanged(snapshot?.size() ?: 0)
            }
    }
    fun toggleLike(
        noteId: String,
        userIdReceptor: String,
        isLiked: Boolean
    ) {
        val currentUser = auth.currentUser ?: return
        val userIdProvider = currentUser.uid
        val userNameProvider = currentUser.displayName ?: "Alguien"

        val likesRef = db.collection("likes")
        val notificationsRef = db.collection("notifications")

        if (isLiked) {
            val likeId = likesRef.document().id
            val notificationId = notificationsRef.document().id

            val likeData = mapOf(
                "idLike" to likeId,
                "idNoteRelated" to noteId,
                "idUserProvider" to userIdProvider,
                "idUserReceptor" to userIdReceptor
            )

            likesRef.document(likeId).set(likeData)

            // Solo notificar si el receptor no es el mismo usuario
            if (userIdProvider != userIdReceptor) {
                val notificationData = mapOf(
                    "idNotification" to notificationId,
                    "idNoteRelated" to noteId,
                    "idUserProvider" to userIdProvider,
                    "userNameProvider" to userNameProvider,
                    "idUserReceptor" to userIdReceptor,
                    "content" to "¡$userNameProvider te ha dado like en tu nota!",
                    "seen" to false
                )
                notificationsRef.document(notificationId).set(notificationData)
            }
        } else {
            likesRef
                .whereEqualTo("idNoteRelated", noteId)
                .whereEqualTo("idUserProvider", userIdProvider)
                .get()
                .addOnSuccessListener { likeDocs ->
                    for (doc in likeDocs) {
                        likesRef.document(doc.id).delete()
                    }
                }

            notificationsRef
                .whereEqualTo("idNoteRelated", noteId)
                .whereEqualTo("idUserProvider", userIdProvider)
                .whereEqualTo("idUserReceptor", userIdReceptor)
                .get()
                .addOnSuccessListener { notifDocs ->
                    for (doc in notifDocs) {
                        notificationsRef.document(doc.id).delete()
                    }
                }
        }
    }

    fun shareNote(noteId: String, context: android.content.Context, onLinkReady: (String) -> Unit) {
        // Usa el dominio correcto de Dynamic Links
        val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse("https://pawls4eversocial.page.link/note?noteId=$noteId"))
            .setDomainUriPrefix("https://pawls4eversocial.page.link")
            .setAndroidParameters(
                DynamicLink.AndroidParameters.Builder(context.packageName).build()
            )
            .buildDynamicLink()

        val dynamicLinkUri = dynamicLink.uri.toString()

        onLinkReady(dynamicLinkUri)

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "¡Mira este recuerdo en Pawls4Ever! $dynamicLinkUri")
            type = "text/plain"
            setPackage("com.whatsapp")
        }
        context.startActivity(sendIntent)
    }
}

