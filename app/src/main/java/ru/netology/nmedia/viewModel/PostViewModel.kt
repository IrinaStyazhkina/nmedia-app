package ru.netology.nmedia.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.netology.nmedia.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryInMemoryImpl

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    published = "",
)

class PostViewModel: ViewModel() {

    private val repository: PostRepository = PostRepositoryInMemoryImpl()
    val data = repository.getAll()

    private val _edited = MutableLiveData(empty)
    val edited: LiveData<Post> = _edited

    fun save() {
        _edited.value?.let {
            repository.save(it)
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
            if(it.content == text) {
                return
            }
            _edited.value = it.copy(content = text)
        }
    }
    fun likeById(id: Long) = repository.likeById(id)
    fun shareById(id: Long) = repository.shareById(id)
    fun removeById(id: Long) = repository.removeById(id)
}