package ru.netology.nmedia.api

import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.model.PushToken

interface UserApi {
    @FormUrlEncoded
    @POST("users/authentication")
    suspend fun doLogin(@Field("login") login: String, @Field("pass") password: String): AuthState

    @FormUrlEncoded
    @POST("users/registration")
    suspend fun doRegister(@Field("login") login: String, @Field("pass") password: String, @Field("name") name: String): AuthState

    @POST("users/push-tokens")
    suspend fun sendPushToken(@Body token: PushToken)
}