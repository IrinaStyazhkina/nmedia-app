package ru.netology.nmedia.repository

import ru.netology.nmedia.api.UserApiService
import ru.netology.nmedia.auth.AuthState

class UserRepositoryImpl: UserRepository {
    override suspend fun doLogin(login: String, password: String): AuthState {
        return UserApiService.service.doLogin(login, password)
    }

    override suspend fun doRegister(login: String, password: String, name: String): AuthState {
        return UserApiService.service.doRegister(login, password, name)
    }
}