package ru.netology.nmedia.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.model.Attachment
import ru.netology.nmedia.model.AttachmentType
import ru.netology.nmedia.model.Media
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.model.Post
import java.lang.RuntimeException
import javax.inject.Inject
import kotlin.Exception

class PostRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val postApiService: PostApi,
    ) : PostRepository {
    override val data = Pager(
        config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = false,
        ),
        pagingSourceFactory = {
            PostPagingSource(postApiService)
        }
    ).flow

//        postDao.getAll().map {
//        it.map(PostEntity::toDto)
//    }.flowOn(Dispatchers.Default)

    override fun getNewerCount(postId: Long): Flow<Int> =
        flow {
            while (true) {
                try {
                    delay(10_000)

                    val newerPosts = postApiService.getNewer(postId)
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
        val posts = postApiService.getAll()
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
                postApiService.unlikeById(id)
            } else {
                postApiService.likeById(id)
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
            postApiService.deleteById(id)
        } catch (e: Exception) {
            postDao.insert(data)
            throw e
        }
    }

    override suspend fun saveAsync(post: Post): Post {
        return try {
            val created = postApiService.save(post)
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
            val created = postApiService.save(post.copy(attachment = Attachment(
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

        return postApiService.saveMedia(part)
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