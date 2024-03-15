package ru.netology.nmedia.repository

import ru.netology.nmedia.model.Post

interface PostRepository {
    fun getAllAsync(callback: PostsCallback<List<Post>>)
    fun getByIdAsync(id: Long, callback: PostsCallback<Post>)
    fun likeByIdAsync(id: Long, callback: PostsCallback<Post>)
    fun removeByIdAsync(id: Long, callback: PostsCallback<Unit>)
    fun saveAsync(post: Post, callback: PostsCallback<Post>)

    interface PostsCallback<T> {
        fun onSuccess(data: T)
        fun onError(throwable: Throwable)
    }
}