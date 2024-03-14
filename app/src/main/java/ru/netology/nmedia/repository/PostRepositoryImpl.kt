package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.model.Post
import java.lang.RuntimeException
import java.util.concurrent.TimeUnit

class PostRepositoryImpl : PostRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
        private val typeToken = object : TypeToken<List<Post>>(){}
    }

    override fun getAll(): List<Post> {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        return client.newCall(request)
            .execute()
            .let {it.body?.string() ?: throw RuntimeException("body is null")}
            .let { gson.fromJson(it, typeToken)}
    }

    override fun getById(id: Long): Post? {
        val getRequest = Request.Builder()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        return client.newCall(getRequest)
            .execute()
            .let {it.body?.string() ?: throw RuntimeException("No post with provided id")}
            .let { gson.fromJson(it, Post::class.java)}
    }

    override fun likeById(id: Long) {
        val post = getById(id) ?: return
        if(post.likedByMe) {
            val deleteRequest = Request.Builder()
                .delete()
                .url("${BASE_URL}/api/slow/posts/$id/likes")
                .build()

            client.newCall(deleteRequest)
                .execute()
                .let {it.body?.string() ?: throw RuntimeException("Something went wrong while getting post")}
                .let { gson.fromJson(it, Post::class.java)}
        } else {
            val postRequest = Request.Builder()
                .post(gson.toJson(post).toRequestBody(jsonType))
                .url("${BASE_URL}/api/slow/posts/$id/likes")
                .build()

            client.newCall(postRequest)
                .execute()
                .let {it.body?.string() ?: throw RuntimeException("Something went wrong while getting post")}
                .let { gson.fromJson(it, Post::class.java)}
        }
    }
    override fun removeById(id: Long) {
        val request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }

    override fun save(post: Post) {
        val request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }
}