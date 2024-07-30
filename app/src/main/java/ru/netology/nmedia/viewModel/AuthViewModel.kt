package ru.netology.nmedia.viewModel

import androidx.lifecycle.ViewModel
import ru.netology.nmedia.auth.AppAuth

class AuthViewModel : ViewModel() {

    val data = AppAuth.getInstance().authState

    val authenticated: Boolean
        get() = data.value.id != 0L
}