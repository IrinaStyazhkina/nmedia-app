package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import ru.netology.nmedia.BASE_URL
import ru.netology.nmedia.model.Post
import java.io.IOException
import java.lang.Exception
import java.lang.RuntimeException
import java.util.concurrent.TimeUnit

class PostRepositoryImpl : PostRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()

    companion object {
        private val jsonType = "application/json".toMediaType()
        private val typeToken = object : TypeToken<List<Post>>() {}
    }

    override fun getAllAsync(callback: PostRepository.PostsCallback<List<Post>>) {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        return client.newCall(request)
            .enqueue(
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        callback.onError(e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val responseText = response.body?.string()
                        if (responseText == null) {
                            callback.onError(RuntimeException("body is null"))
                            return
                        }
                        try {
                            callback.onSuccess(gson.fromJson(responseText, typeToken))
                        } catch (e: Exception) {
                            callback.onError(e)
                        }
                    }
                }
            )

    }

    override fun getByIdAsync(id: Long, callback: PostRepository.PostsCallback<Post>) {
        val getRequest = Request.Builder()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        return client.newCall(getRequest)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseText = response.body?.string()
                    if (responseText == null) {
                        callback.onError(RuntimeException("No post with provided id"))
                        return
                    }
                    try {
                        callback.onSuccess(gson.fromJson(responseText, Post::class.java))
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }
            })
    }

    override fun likeByIdAsync(id: Long, callback: PostRepository.PostsCallback<Post>) {
        getByIdAsync(id, object : PostRepository.PostsCallback<Post> {
            override fun onSuccess(data: Post) {
                val request = if (data.likedByMe) {
                    Request.Builder()
                        .delete()
                        .url("${BASE_URL}/api/slow/posts/$id/likes")
                        .build()
                } else {
                    Request.Builder()
                        .post(gson.toJson(data).toRequestBody(jsonType))
                        .url("${BASE_URL}/api/slow/posts/$id/likes")
                        .build()
                }
                client.newCall(request)
                    .enqueue(
                        object : Callback {
                            override fun onFailure(call: Call, e: IOException) {
                                callback.onError(e)
                            }

                            override fun onResponse(call: Call, response: Response) {
                                val responseText = response.body?.string()
                                if (responseText == null) {
                                    callback.onError(RuntimeException("No post with provided id"))
                                    return
                                }
                                try {
                                    callback.onSuccess(
                                        gson.fromJson(
                                            responseText,
                                            Post::class.java
                                        )
                                    )
                                } catch (e: Exception) {
                                    callback.onError(e)
                                }
                            }
                        }
                    )
            }

            override fun onError(throwable: Throwable) {
                callback.onError(throwable)
            }
        })
    }


    override fun removeByIdAsync(id: Long, callback: PostRepository.PostsCallback<Unit>) {
        val request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        client.newCall(request)
            .enqueue(
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        callback.onError(e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        callback.onSuccess(Unit)
                    }
                }
            )
    }

    override fun saveAsync(post: Post, callback: PostRepository.PostsCallback<Post>) {
        val request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .enqueue(
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        callback.onError(e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val responseText = response.body?.string()
                        if (responseText == null) {
                            callback.onError(RuntimeException("No post with provided id"))
                            return
                        }
                        try {
                            callback.onSuccess(
                                gson.fromJson(
                                    responseText,
                                    Post::class.java
                                )
                            )
                        } catch (e: Exception) {
                            callback.onError(e)
                        }
                    }
                }
            )
    }
}