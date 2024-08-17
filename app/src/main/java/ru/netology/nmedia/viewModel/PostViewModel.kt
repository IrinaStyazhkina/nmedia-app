package ru.netology.nmedia.viewModel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.model.FeedItem
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.utils.PostDeletedData
import ru.netology.nmedia.utils.SingleLiveEvent
import java.io.File
import javax.inject.Inject

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    authorId = -1L,
    likedByMe = false,
    published = 0L,
    authorAvatar = "",
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PostViewModel @Inject constructor(
    private val postRepository: PostRepository,
    appAuth: AppAuth,
) : ViewModel() {

    val authData = appAuth.authState

    val authenticated: Boolean
        get() = authData.value.id != 0L

    private val _state = MutableLiveData(FeedModelState())
    val state: LiveData<FeedModelState>
        get() = _state

    private val _photo = MutableLiveData<PhotoModel?>()
    val photo: LiveData<PhotoModel?>
        get() = _photo

    val data: Flow<PagingData<FeedItem>> = appAuth
        .authState
        .flatMapLatest { auth ->
            postRepository.data.map { posts ->
                posts.map { post ->
                    if(post is Post) {
                        post.copy(ownedByMe = auth.id == post.authorId)
                    } else {
                        post
                    }
                }
        }
    }.flowOn(Dispatchers.Default)

//    val newerCount: LiveData<Int> = data.switchMap {
//        val firstId = it.posts.firstOrNull()?.id ?: 0L
//
//        postRepository.getNewerCount(firstId)
//            .asLiveData(Dispatchers.Default)
//    }

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

    fun loadPosts() {
        //_state.postValue(FeedModelState(loading = true))

        viewModelScope.launch {
            try {
                postRepository.getAllAsync()
                _state.value = FeedModelState()
            } catch (e: Exception) {
                _state.value = FeedModelState(loading = false, error = true)
            }
        }
    }

    fun setPhoto(uri: Uri, file: File) {
        _photo.value = PhotoModel(uri, file)
    }

    fun clearPhoto() {
        _photo.value = null
    }

    fun save() {
        viewModelScope.launch {
            _edited.value?.let { post ->
                try {
                    photo.value?.let {
                        postRepository.saveAsyncWithAttachment(post.copy(author = "Netology"), it)
                    } ?: postRepository.saveAsync(post.copy(author = "Netology"))
                    _postCreated.postValue(true)
                    _edited.value = empty
                    _photo.value = null
                } catch (e: Exception) {
                    _postCreated.postValue(false)
                }
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
                postRepository.likeByIdAsync(id)
                _postLikeChanged.postValue(true)
            } catch (e: Exception) {
                _postLikeChanged.postValue(false)
            }
        }
    }

    fun removeById(id: Long) {
        viewModelScope.launch {
            try {
                postRepository.removeByIdAsync(id)
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


    fun readAllPosts() {
        viewModelScope.launch {
            postRepository.readAllPosts()
            _postHiddenCountChanged.postValue(0)
        }
    }
}