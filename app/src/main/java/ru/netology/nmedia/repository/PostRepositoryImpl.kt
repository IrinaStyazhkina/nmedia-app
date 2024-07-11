package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.netology.nmedia.api.PostApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.model.Post
import java.lang.RuntimeException
import kotlin.Exception

class PostRepositoryImpl(private val postDao: PostDao) : PostRepository {
    override val data: LiveData<List<Post>> = postDao.getAll().map {
        it.map(PostEntity::toDto)
    }

    override suspend fun getAllAsync() {
        val posts = PostApiService.service.getAll()
        postDao.insert(posts.map {
            PostEntity.fromDto(it)
        })
    }

    override suspend fun getByIdAsync(id: Long): Post {
        return postDao.getById(id)?.toDto() ?: throw RuntimeException("No post with provided id")
    }

    override suspend fun likeByIdAsync(id: Long): Post {
        val data = postDao.getById(id) ?: throw RuntimeException("No post with provided id")
        return try {
            postDao.likeById(id)
            if (data.likedByMe) {
                PostApiService.service.unlikeById(id)
            } else {
                PostApiService.service.likeById(id)
            }
        } catch (e: Exception) {
            postDao.likeById(id)
            throw e
        }
    }


    override suspend fun removeByIdAsync(id: Long) {
        val data = postDao.getById(id) ?: throw RuntimeException("No post with provided id")
        try {
            postDao.removeById(id)
            PostApiService.service.deleteById(id)
        } catch (e: Exception) {
            postDao.insert(data)
            throw e
        }
    }

    override suspend fun saveAsync(post: Post): Post {
        return try {
            val created = PostApiService.service.save(post)
            postDao.save(PostEntity.fromDto(created))
            created
        } catch (e: Exception) {
            e.printStackTrace()
            post
        }
    }
}