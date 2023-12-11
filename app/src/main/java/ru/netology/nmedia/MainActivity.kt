package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import androidx.activity.viewModels
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.viewModel.PostViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()
        viewModel.data.observe(this) {
            with(binding) {
                tvAuthor.text = it.author
                tvPublished.text = it.published
                tvContent.text = Html.fromHtml(it.content)
                tvLike.text = formatCount(it.likes)
                tvShare.text = formatCount(it.shares)
                tvSeen.text = formatCount(it.seen)

                ivLike.setImageResource(
                    if(it.likedByMe) R.drawable.liked_24 else R.drawable.like_24
                )

                ivLike.setOnClickListener {
                    viewModel.like()
                }

                ivShare.setOnClickListener {
                    viewModel.share()
                }
            }
        }
    }
}