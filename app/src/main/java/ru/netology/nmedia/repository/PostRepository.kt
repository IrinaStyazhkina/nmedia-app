package ru.netology.nmedia.repository

import ru.netology.nmedia.model.Post

interface PostRepository {
    fun getAll(): List<Post>
    fun getById(id: Long): Post?
    fun likeById(id: Long)
    fun removeById(id: Long)
    fun save(post: Post)
}