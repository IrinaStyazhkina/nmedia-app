package ru.netology.nmedia.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.utils.SingleLiveEvent

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    published = "",
)

class PostViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _edited = MutableLiveData(empty)
    val edited: LiveData<Post>
        get() = _edited

    init {
        loadPosts()
    }

    fun loadPosts() {
        _data.postValue(FeedModel(loading = true))
        repository.getAllAsync(object : PostRepository.PostsCallback<List<Post>> {
            override fun onSuccess(data: List<Post>) {
                _data.postValue(FeedModel(posts = data, empty = data.isEmpty()))
            }

            override fun onError(throwable: Throwable) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun save() {
        _edited.value?.let {
            repository.saveAsync(
                it.copy(author = "Netology"),
                object : PostRepository.PostsCallback<Post> {
                    override fun onSuccess(data: Post) {
                        _postCreated.postValue(Unit)
                    }

                    override fun onError(throwable: Throwable) {}
                })
        }
        _edited.value = empty
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
        val old = _data.value?.posts.orEmpty()
        _data.postValue(
            _data.value?.copy(posts = _data.value?.posts.orEmpty().map {
                if (it.id != id) it else it.copy(
                    likes = if (it.likedByMe) it.likes - 1 else it.likes + 1,
                    likedByMe = !it.likedByMe
                )
            })
        )

        repository.likeByIdAsync(id, object : PostRepository.PostsCallback<Post> {
            override fun onSuccess(data: Post) {}
            override fun onError(throwable: Throwable) {
                _data.postValue(_data.value?.copy(posts = old))
            }
        })
    }

    fun removeById(id: Long) {
        val old = _data.value?.posts.orEmpty()
        val newPosts = _data.value?.posts.orEmpty().filter {
            it.id != id
        }
        _data.postValue(FeedModel(posts = newPosts, empty = newPosts.isEmpty()))

        repository.removeByIdAsync(id, object : PostRepository.PostsCallback<Unit> {
            override fun onSuccess(data: Unit) {}

            override fun onError(throwable: Throwable) {
                _data.postValue(_data.value?.copy(posts = old))
            }
        })
    }

}