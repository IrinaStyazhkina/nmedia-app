package ru.netology.nmedia.model


sealed interface FeedItem {
    val id: Long
    val published: Long
}
data class Post(
    override val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    override val published: Long,
    val likes: Int = 0,
    val shares: Int = 0,
    val likedByMe: Boolean = false,
    val attachment: Attachment? = null,
    val ownedByMe: Boolean  = false,
    ): FeedItem

data class Ad(
    override val id: Long,
    val image: String,
    override val published: Long,
    ): FeedItem

data class TimeSeparator(
    override val id: Long,
    val text: TimeSeparatorValue,
    override val published: Long = 0L,
): FeedItem
