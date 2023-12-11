package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.Post

class PostRepositoryInMemoryImpl: PostRepository {

    private var post = Post(
        id = 1,
        author = "Нетология. Университет интернет-пофессий будущего",
        content = "Привет, это новая нетология! Когда-то нетология начиналась с интенсивов по онлайн маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растем сами и помогаем расти студентам: от новичков до уверенных профессионалов.Но самое важное остается с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия - помочь встать на путь роста и начать цепочку перемен - <a href=\"http://netolo.gy/fyb\">http://netolo.gy/fyb</a>",
        published = "21 мая в 18:36",
        likes = 999,
        shares = 5,
        seen = 20,
        likedByMe = false,
    )

    private val data = MutableLiveData(post)
    override fun get(): LiveData<Post> = data
    override fun like() {
        post = post.copy(likedByMe = !post.likedByMe, likes = if(post.likedByMe) post.likes - 1 else post.likes + 1)
        data.value = post
    }

    override fun share() {
        post = post.copy(shares = post.shares + 1)
        data.value = post
    }
}