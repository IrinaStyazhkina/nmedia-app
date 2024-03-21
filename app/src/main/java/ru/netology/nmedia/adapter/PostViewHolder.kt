package ru.netology.nmedia.adapter

import android.text.Html
import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.BASE_URL
import ru.netology.nmedia.OnInteractionListener
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.formatCount
import ru.netology.nmedia.handler.loadContentImage
import ru.netology.nmedia.handler.loadWithCircleCrop
import ru.netology.nmedia.model.AttachmentType

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.apply {
            ivLogo.loadWithCircleCrop("${BASE_URL}/avatars/${post.authorAvatar}")
            tvAuthor.text = post.author
            tvPublished.text = post.published
            tvContent.text = Html.fromHtml(post.content)
            like.text = formatCount(post.likes)
            share.text = formatCount(post.shares)

            like.isChecked = post.likedByMe

            if(post.attachment == null) {
                attachmentImg.visibility = View.GONE
            } else {
                if(post.attachment.type == AttachmentType.IMAGE){
                    attachmentImg.visibility = View.VISIBLE
                    attachmentImg.loadContentImage("${BASE_URL}/images/${post.attachment.url}")
                    attachmentImg.contentDescription = post.attachment.description
                }
            }

            tvContent.setOnClickListener {
                onInteractionListener.onOpen(post)
            }

            tvAuthor.setOnClickListener {
                onInteractionListener.onOpen(post)
            }

            tvPublished.setOnClickListener {
                onInteractionListener.onOpen(post)
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