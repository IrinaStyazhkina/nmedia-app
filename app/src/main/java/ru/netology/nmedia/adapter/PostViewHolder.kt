package ru.netology.nmedia.adapter

import android.text.Html
import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.OnInteractionListener
import ru.netology.nmedia.Post
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.formatCount

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.apply {
            tvAuthor.text = post.author
            tvPublished.text = post.published
            tvContent.text = Html.fromHtml(post.content)
            like.text = formatCount(post.likes)
            share.text = formatCount(post.shares)
            tvSeen.text = formatCount(post.seen)

            like.isChecked = post.likedByMe

            if (post.video != null) {
                videoGroup.visibility = View.VISIBLE
                videoImg.setOnClickListener {
                   onInteractionListener.onPlay(post)
                }
                play.setOnClickListener {
                    onInteractionListener.onPlay(post)
                }
            } else {
                videoGroup.visibility = View.GONE
            }

            like.setOnClickListener {
                onInteractionListener.onLike(post)
            }

            share.setOnClickListener {
                onInteractionListener.onShare(post)
            }

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when(item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }
                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }
        }
    }
}