package ru.netology.nmedia.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.model.Post

interface PostRepository {

    val data: Flow<PagingData<Post>>
    fun getNewerCount(postId: Long): Flow<Int>
    suspend fun getAllAsync()
    suspend fun getByIdAsync(id: Long): Post
    suspend fun likeByIdAsync(id: Long): Post
    suspend fun removeByIdAsync(id: Long)
    suspend fun saveAsync(post: Post): Post

    suspend fun saveAsyncWithAttachment(post: Post, model: PhotoModel): Post
    suspend fun readAllPosts()
    suspend fun getUnreadCount(): Int

}