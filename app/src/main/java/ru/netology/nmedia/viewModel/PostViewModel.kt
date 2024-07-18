package ru.netology.nmedia.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.utils.PostDeletedData
import ru.netology.nmedia.utils.SingleLiveEvent

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    published = "",
    authorAvatar = "",
)

class PostViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PostRepository = PostRepositoryImpl(AppDb.getInstance(application).postDao())

    private val _state = MutableLiveData(FeedModelState())
    val state:LiveData<FeedModelState>
        get() = _state

    val data: LiveData<FeedModel> = repository.data.map {
        FeedModel(
            posts = it,
            empty = it.isEmpty()
        )
    }.asLiveData(Dispatchers.Default)

    val newerCount: LiveData<Int> = data.switchMap {
        val firstId = it.posts.firstOrNull()?.id ?: 0L

        repository.getNewerCount(firstId)
            .asLiveData(Dispatchers.Default)
    }


    private val _postCreated = SingleLiveEvent<Boolean>()
    val postCreated: LiveData<Boolean>
        get() = _postCreated

    private val _postDeleted = SingleLiveEvent<PostDeletedData>()
    val postDeleted: LiveData<PostDeletedData>
        get() = _postDeleted

    private val _postLikeChanged = SingleLiveEvent<Boolean>()
    val postLikeChanged: LiveData<Boolean>
        get() = _postLikeChanged

    private val _edited = MutableLiveData(empty)
    val edited: LiveData<Post>
        get() = _edited

    private val _postHiddenCountChanged = SingleLiveEvent<Int>()
    val postHiddenCountChanged: LiveData<Int>
        get() = _postHiddenCountChanged

    init {
        loadPosts()
    }

    fun loadPosts() {
        _state.postValue(FeedModelState(loading = true))

        viewModelScope.launch {
            try {
                repository.getAllAsync()
                _state.value = FeedModelState()
            } catch (e: Exception) {
                _state.value = FeedModelState(loading = false, error = true)
            }
        }
    }

    fun save() {
        viewModelScope.launch {
            try {
                _edited.value?.let {
                    repository.saveAsync(it.copy(author = "Netology"))
                    _postCreated.postValue(true)
                }
                _edited.value = empty
            }
            catch (e: Exception) {
                _postCreated.postValue(false)
            }
        }
    }

    fun edit(post: Post) {
        _edited.value = post
    }

    fun cancelEdit() {
        _edited.value = empty
    }

    fun changeContent(content: String) {
        _edited.value?.let {
            val text = content.trim()
            if (it.content == text) {
                return
            }
            _edited.value = it.copy(content = text)
        }
    }

    fun likeById(id: Long) {
        viewModelScope.launch {
            try {
                repository.likeByIdAsync(id)
                _postLikeChanged.postValue(true)
            } catch (e: Exception) {
                _postLikeChanged.postValue(false)
            }
        }
    }

    fun removeById(id: Long) {
        viewModelScope.launch {
            try {
                repository.removeByIdAsync(id)
                _postDeleted.postValue(
                    PostDeletedData(
                    isSuccessful = true,
                    id
                )
                )
            } catch (e: Exception) {
                _postDeleted.postValue(
                    PostDeletedData(
                    isSuccessful = false,
                    id
                )
                )
            }
        }
    }


    fun refresh() {
        _state.postValue(FeedModelState(refreshing = true))

        viewModelScope.launch {
            try {
                repository.getAllAsync()
                _state.value = FeedModelState()
                _postHiddenCountChanged.postValue(0)
            } catch (e: Exception) {
                _state.value = FeedModelState(refreshing = false, error = true)
            }
        }
    }

    fun readAllPosts() {
        viewModelScope.launch {
            repository.readAllPosts()
            _postHiddenCountChanged.postValue(0)
        }
    }

    fun getUnreadPostsCount() {
        viewModelScope.launch{
            val count = repository.getUnreadCount()
            _postHiddenCountChanged.postValue(count)
        }
    }
}