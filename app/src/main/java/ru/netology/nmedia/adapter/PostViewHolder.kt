package ru.netology.nmedia.adapter

import android.text.Html
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.Post
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.formatCount

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onLikeListener: OnLikeListener,
    private val onShareListener: OnShareListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.apply {
            tvAuthor.text = post.author
            tvPublished.text = post.published
            tvContent.text = Html.fromHtml(post.content)
            tvLike.text = formatCount(post.likes)
            tvShare.text = formatCount(post.shares)
            tvSeen.text = formatCount(post.seen)

            ivLike.setImageResource(
                if (post.likedByMe) R.drawable.liked_24 else R.drawable.like_24
            )

            ivLike.setOnClickListener {
                onLikeListener(post)
            }

            ivShare.setOnClickListener {
                onShareListener(post)
            }
        }
    }
}