package ru.netology.nmedia.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.repository.UserRepository
import ru.netology.nmedia.utils.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val appAuth: AppAuth,
) : ViewModel()  {

    private val _state = MutableLiveData(FeedModelState())
    val state: LiveData<FeedModelState>
        get() = _state

    private val _userAuthorized= SingleLiveEvent<Boolean>()
    val userAuthorized: LiveData<Boolean>
        get() = _userAuthorized

    fun doRegister(login: String, password: String, name: String) {
        viewModelScope.launch {
            _state.value = FeedModelState(loading = true)
            try {
                val result = userRepository.doRegister(login, password, name)
                if(result.id != null && result.token != null) {
                    appAuth.setAuth(result.id, result.token)
                }
                _userAuthorized.postValue(true)
                _state.value = FeedModelState()
            } catch (e: Exception) {
                _state.value = FeedModelState(error = true)
                _userAuthorized.postValue(false)
            }
        }
    }
}