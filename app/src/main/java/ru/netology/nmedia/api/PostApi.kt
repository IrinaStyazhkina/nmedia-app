package ru.netology.nmedia.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import ru.netology.nmedia.BASE_URL
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.model.Post
import java.util.concurrent.TimeUnit

private val client = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .let {
        if (BuildConfig.DEBUG) {
            it.addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        } else {
            it
        }
    }
    .build()


private val retrofit = Retrofit.Builder()
    .baseUrl("${BASE_URL}/api/slow/")
    .client(client)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

interface PostApi {

    @GET("posts")
    suspend fun getAll(): List<Post>

    @GET("posts/{id}")
    suspend fun getById(@Path("id") id: Long): Post

    @POST("posts")
    suspend fun save(@Body post: Post): Post

    @DELETE("posts/{id}")
    suspend fun deleteById(@Path("id") id: Long)

    @POST("posts/likes/{id}")
    suspend fun likeById(@Path("id") id: Long): Post

    @DELETE("posts/likes/{id}")
    suspend fun unlikeById(@Path("id") id: Long): Post
}

object PostApiService {
    val service: PostApi by lazy {
        retrofit.create()
    }
}