package ru.netology.nmedia.repository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.api.PostApiService
import ru.netology.nmedia.model.Post
import java.lang.Exception
import java.lang.RuntimeException

class PostRepositoryImpl : PostRepository {

    override fun getAllAsync(callback: PostRepository.PostsCallback<List<Post>>) {

        return PostApiService.service
            .getAll()
            .enqueue(
                object : Callback<List<Post>> {
                    override fun onResponse(
                        call: Call<List<Post>>,
                        response: Response<List<Post>>
                    ) {
                        if (!response.isSuccessful) {
                            callback.onError(Exception(response.errorBody()?.string()))
                            return
                        }
                        val body = response.body() ?: throw RuntimeException("body is null")
                        try {
                            callback.onSuccess(body)
                        } catch (e: Exception) {
                            callback.onError(e)
                        }
                    }

                    override fun onFailure(call: Call<List<Post>>, error: Throwable) {
                        callback.onError(Exception(error))
                    }
                }
            )

    }

    override fun getByIdAsync(id: Long, callback: PostRepository.PostsCallback<Post>) {

        return PostApiService.service
            .getById(id)
            .enqueue(object : Callback<Post> {
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (!response.isSuccessful) {
                        callback.onError(Exception(response.errorBody()?.string()))
                        return
                    }
                    val body = response.body() ?: throw RuntimeException("No post with provided id")
                    try {
                        callback.onSuccess(body)
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }

                override fun onFailure(call: Call<Post>, error: Throwable) {
                    callback.onError(Exception(error))
                }
            })
    }

    override fun likeByIdAsync(id: Long, callback: PostRepository.PostsCallback<Post>) {
        getByIdAsync(id, object : PostRepository.PostsCallback<Post> {
            override fun onSuccess(data: Post) {
                val call = if (data.likedByMe) {
                    PostApiService.service.unlikeById(id)
                } else {
                    PostApiService.service.likeById(id)
                }

                call
                    .enqueue(
                        object : Callback<Post> {
                            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                                if (!response.isSuccessful) {
                                    callback.onError(Exception(response.errorBody()?.string()))
                                    return
                                }
                                val body = response.body()
                                    ?: throw RuntimeException("No post with provided id")

                                try {
                                    callback.onSuccess(body)
                                } catch (e: Exception) {
                                    callback.onError(e)
                                }
                            }

                            override fun onFailure(call: Call<Post>, error: Throwable) {
                                callback.onError(Exception(error))
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
        PostApiService.service
            .deleteById(id)
            .enqueue(
                object : Callback<Unit> {
                    override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                        if (!response.isSuccessful) {
                            callback.onError(Exception(response.errorBody()?.string()))
                            return
                        }
                        callback.onSuccess(Unit)
                    }

                    override fun onFailure(call: Call<Unit>, error: Throwable) {
                        callback.onError(Exception(error))
                    }
                }
            )
    }

    override fun saveAsync(post: Post, callback: PostRepository.PostsCallback<Post>) {
        PostApiService.service
            .save(post)
            .enqueue(
                object : Callback<Post> {
                    override fun onResponse(call: Call<Post>, response: Response<Post>) {
                        if (!response.isSuccessful) {
                            callback.onError(Exception(response.errorBody()?.string()))
                            return
                        }
                        val body = response.body()
                            ?: throw RuntimeException("Something went wrong while saving post")
                        try {
                            callback.onSuccess(body)
                        } catch (e: Exception) {
                            callback.onError(e)
                        }
                    }

                    override fun onFailure(call: Call<Post>, error: Throwable) {
                        callback.onError(Exception(error))
                    }
                }
            )
    }
}