package ru.netology.nmedia.repository

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nmedia.api.PostApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.model.Attachment
import ru.netology.nmedia.model.AttachmentType
import ru.netology.nmedia.model.Media
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.model.Post
import java.lang.RuntimeException
import kotlin.Exception

class PostRepositoryImpl(private val postDao: PostDao) : PostRepository {
    override val data: Flow<List<Post>> = postDao.getAll().map {
        it.map(PostEntity::toDto)
    }.flowOn(Dispatchers.Default)

    override fun getNewerCount(postId: Long): Flow<Int> =
        flow {
            while (true) {
                try {
                    delay(10_000)

                    val newerPosts = PostApiService.service.getNewer(postId)
                    postDao.insert(newerPosts.map { PostEntity.fromDto(it).copy(hidden = true) })
                    emit(newerPosts.size)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
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

    override suspend fun saveAsyncWithAttachment(post: Post, model: PhotoModel): Post {
        return try {
            val media = upload(model)
            val created = PostApiService.service.save(post.copy(attachment = Attachment(
                url = media.id,
                type = AttachmentType.IMAGE,
                description = "",
            )))
            postDao.save(PostEntity.fromDto(created))
            created
        } catch (e: Exception) {
            e.printStackTrace()
            post
        }
    }

    private suspend fun upload(photoModel: PhotoModel): Media {
        val part = MultipartBody.Part.createFormData("file",
            photoModel.file.name,
            photoModel.file.asRequestBody())

        return PostApiService.service.saveMedia(part)
    }

    override suspend fun readAllPosts() {
        try {
            postDao.readAll()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun getUnreadCount(): Int {
        return try {
            postDao.getUnreadCount()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            0
        }
    }
}