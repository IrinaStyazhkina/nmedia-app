package ru.netology.nmedia

import ru.netology.nmedia.model.Post

interface OnInteractionListener {
    fun onLike(post: Post) {}
    fun onShare(post: Post) {}
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onOpen(post: Post) {}
}