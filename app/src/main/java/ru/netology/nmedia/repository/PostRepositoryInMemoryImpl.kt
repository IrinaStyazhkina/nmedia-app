package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.Post

private var nextId = 2L

class PostRepositoryInMemoryImpl: PostRepository {

    private var posts = listOf<Post>(Post(
        id = 2,
        author = "Нетология. Университет интернет-пофессий будущего",
        content = "Привет, это новая нетология! Когда-то нетология начиналась с интенсивов по онлайн маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растем сами и помогаем расти студентам: от новичков до уверенных профессионалов.Но самое важное остается с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия - помочь встать на путь роста и начать цепочку перемен - <a href=\"http://netolo.gy/fyb\">http://netolo.gy/fyb</a>",
        published = "21 мая в 18:36",
        likes = 999,
        shares = 5,
        seen = 20,
        likedByMe = false,
        video = "https://www.youtube.com/watch?v=WhWc3b3KhnY"
    ), Post(
        id = 1,
        author = "Нетология. Университет интернет-пофессий будущего",
        content = "Второй пост про нетологию",
        published = "22 мая в 18:36",
        likes = 5,
        shares = 7,
        seen = 9,
        likedByMe = false,
    ))

    private val data = MutableLiveData(posts)
    override fun getAll(): LiveData<List<Post>> = data
    override fun likeById(id: Long) {
        posts = posts.map {
            if(it.id != id) it else it.copy(likedByMe = !it.likedByMe, likes = if(it.likedByMe) it.likes - 1 else it.likes + 1)
        }
        data.value = posts
    }

    override fun shareById(id: Long) {
        posts = posts.map {
            if(it.id != id) it else it.copy(shares = it.shares + 1)
        }
        data.value = posts
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }
        data.value = posts
    }

    override fun save(post: Post) {
        if(post.id == 0L) {
            posts = listOf(
                post.copy(
                    id = ++nextId,
                    author="Нетология",
                    likedByMe = false,
                    published = "now"
                )
            ) + posts
            data.value = posts
            return
        }

        posts = posts.map {
            if (it.id != post.id) it else it.copy(content = post.content)
        }
        data.value = posts
    }
}