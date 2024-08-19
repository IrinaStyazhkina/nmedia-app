package ru.netology.nmedia.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.TerminalSeparatorType
import androidx.paging.insertSeparators
import androidx.paging.map
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dao.PostRemoteKeyDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.model.Ad
import ru.netology.nmedia.model.Attachment
import ru.netology.nmedia.model.AttachmentType
import ru.netology.nmedia.model.Media
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.model.TimeSeparator
import ru.netology.nmedia.model.TimeSeparatorValue
import ru.netology.nmedia.utils.DateUtils
import java.lang.RuntimeException
import javax.inject.Inject
import kotlin.Exception
import kotlin.random.Random


class PostRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val postApiService: PostApi,
    private val appAuth: AppAuth,
    postRemoteKeyDao: PostRemoteKeyDao,
    appDb: AppDb,
) : PostRepository {
    @OptIn(ExperimentalPagingApi::class)
    override val data = Pager(
        config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = false,
            maxSize = 200,
        ),
        pagingSourceFactory = { postDao.pagingSource() },
        remoteMediator = PostRemoteMediator(postApiService, postDao, postRemoteKeyDao, appDb)
    )
        .flow.map { pagingData ->
            pagingData.map {
                it.toDto()
            }
                .insertSeparators { prev, _ ->
                    if (prev is Post && prev.id.rem(5) == 0L) {
                        Ad(Random.nextLong(), "figma.jpg", prev.published)
                    } else null
                }.insertSeparators(TerminalSeparatorType.SOURCE_COMPLETE) { prev, next ->
                    if(prev == null && next != null && DateUtils.isToday(next.published)) {
                        TimeSeparator(Random.nextLong(), TimeSeparatorValue.TODAY)
                    } else if(prev != null && next is Post && DateUtils.isToday(prev.published) && DateUtils.isYesterday(next.published)) {
                        TimeSeparator(Random.nextLong(), TimeSeparatorValue.YESTERDAY)
                    } else if(prev != null && next is Post && DateUtils.isYesterday(prev.published) && DateUtils.isLastWeek(next.published)) {
                        TimeSeparator(Random.nextLong(), TimeSeparatorValue.LAST_WEEK)
                    }
                    else null
                }
        }

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
        return
//        val posts = postApiService.getAll()
//        postDao.insert(posts.map {
//            PostEntity.fromDto(it)
//        })
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
            val created = postApiService.save(
                post.copy(
                    attachment = Attachment(
                        url = media.id,
                        type = AttachmentType.IMAGE,
                        description = "",
                    )
                )
            )
            postDao.save(PostEntity.fromDto(created))
            created
        } catch (e: Exception) {
            e.printStackTrace()
            post
        }
    }

    private suspend fun upload(photoModel: PhotoModel): Media {
        val part = MultipartBody.Part.createFormData(
            "file",
            photoModel.file.name,
            photoModel.file.asRequestBody()
        )

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