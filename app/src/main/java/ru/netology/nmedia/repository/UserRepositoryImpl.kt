package ru.netology.nmedia.repository

import ru.netology.nmedia.api.UserApi
import ru.netology.nmedia.auth.AuthState
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApiService: UserApi,
): UserRepository {
    override suspend fun doLogin(login: String, password: String): AuthState {
        return userApiService.doLogin(login, password)
    }

    override suspend fun doRegister(login: String, password: String, name: String): AuthState {
        return userApiService.doRegister(login, password, name)
    }
}