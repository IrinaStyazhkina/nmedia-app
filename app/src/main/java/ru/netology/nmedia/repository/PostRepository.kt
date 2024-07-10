package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.model.Post

interface PostRepository {

    val data: LiveData<List<Post>>
    suspend fun getAllAsync()
    suspend fun getByIdAsync(id: Long): Post
    suspend fun likeByIdAsync(id: Long): Post
    suspend fun removeByIdAsync(id: Long)
    suspend fun saveAsync(post: Post): Post

}