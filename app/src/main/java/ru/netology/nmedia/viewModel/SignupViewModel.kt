package ru.netology.nmedia.viewModel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.repository.UserRepository
import ru.netology.nmedia.repository.UserRepositoryImpl
import ru.netology.nmedia.utils.SingleLiveEvent

class SignupViewModel(application: Application) : AndroidViewModel(application)  {

    private val repository: UserRepository = UserRepositoryImpl()

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
                val result = repository.doRegister(login, password, name)
                if(result.id != null && result.token != null) {
                    AppAuth.getInstance().setAuth(result.id, result.token)
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