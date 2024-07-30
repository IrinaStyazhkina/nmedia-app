package ru.netology.nmedia.repository

import ru.netology.nmedia.auth.AuthState

interface UserRepository {
    suspend fun doLogin(login: String, password: String): AuthState

    suspend fun doRegister(login: String, password: String, name: String): AuthState

}