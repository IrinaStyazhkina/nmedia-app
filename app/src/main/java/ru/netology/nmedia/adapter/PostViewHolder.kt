package ru.netology.nmedia.adapter

import android.text.Html
import android.view.View
import android.widget.PopupMenu
import androidx.core.view.isVisible
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
import ru.netology.nmedia.utils.DateUtils

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.apply {
            ivLogo.loadWithCircleCrop("${BASE_URL}/avatars/${post.authorAvatar}")
            tvAuthor.text = post.author
            tvPublished.text = DateUtils.getDate(post.published)
            tvContent.text = Html.fromHtml(post.content)
            like.text = formatCount(post.likes)
            share.text = formatCount(post.shares)

            like.isChecked = post.likedByMe

            if(post.attachment == null) {
                attachmentImg.visibility = View.GONE
            } else {
                if(post.attachment.type == AttachmentType.IMAGE){
                    attachmentImg.visibility = View.VISIBLE
                    attachmentImg.loadContentImage("${BASE_URL}/media/${post.attachment.url}")
                    attachmentImg.contentDescription = post.attachment.description
                    attachmentImg.setOnClickListener{
                        onInteractionListener.onClickPhoto(post.attachment)
                    }
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

            menu.isVisible = post.ownedByMe

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