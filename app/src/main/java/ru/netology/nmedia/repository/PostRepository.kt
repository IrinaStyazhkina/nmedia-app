package ru.netology.nmedia.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.model.Post

interface PostRepository {

    val data: Flow<List<Post>>
    fun getNewerCount(postId: Long): Flow<Int>
    suspend fun getAllAsync()
    suspend fun getByIdAsync(id: Long): Post
    suspend fun likeByIdAsync(id: Long): Post
    suspend fun removeByIdAsync(id: Long)
    suspend fun saveAsync(post: Post): Post
    suspend fun readAllPosts()
    suspend fun getUnreadCount(): Int

}