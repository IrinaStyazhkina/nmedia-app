package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import ru.netology.nmedia.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val post = Post(
            id = 1,
            author = "Нетология. Университет интернет-пофессий будущего",
            content = "Привет, это новая нетология! Когда-то нетология начиналась с интенсивов по онлайн маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растем сами и помогаем расти студентам: от новичков до уверенных профессионалов.Но самое важное остается с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия - помочь встать на путь роста и начать цепочку перемен - <a href=\"http://netolo.gy/fyb\">http://netolo.gy/fyb</a>",
            published = "21 мая в 18:36",
            likes = 999,
            shares = 5,
            seen = 20,
            likedByMe = false,
        )

        with(binding) {
            tvAuthor.text = post.author
            tvPublished.text = post.published
            tvContent.text = Html.fromHtml(post.content)
            tvLike.text = formatCount(post.likes)
            tvShare.text = formatCount(post.shares)
            tvSeen.text = formatCount(post.seen)

            if(post.likedByMe) {
                ivLike.setImageResource(R.drawable.liked_24)
            }

            ivLike.setOnClickListener {
                post.likedByMe = !post.likedByMe
                if (post.likedByMe) {
                    ivLike.setImageResource(R.drawable.liked_24)
                    post.likes += 1
                } else {
                    ivLike.setImageResource(R.drawable.like_24)
                    post.likes -= 1
                }
                tvLike.text = formatCount(post.likes)
            }

            ivShare.setOnClickListener {
                post.shares += 1
                tvShare.text = formatCount(post.shares)
            }
        }
    }
}