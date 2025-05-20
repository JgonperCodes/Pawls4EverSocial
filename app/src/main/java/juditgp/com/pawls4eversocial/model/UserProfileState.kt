package juditgp.com.pawls4eversocial.model

data class UserProfileState(
    val userId: String = "",
    val username: String = "",
    val email: String = "",
    val description: String = "",
    val profileImageUrl: String = "",
    val bannerUrl: String = "",
    val notesCount: Int = 0,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val isOwnProfile: Boolean = false,
    val isFollowing: Boolean = false,
    val pets: List<Pet> = emptyList(),
    val posts: List<Note> = emptyList(),
    val likedPosts: List<Note> = emptyList()
)