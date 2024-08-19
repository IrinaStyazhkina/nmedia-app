package ru.netology.nmedia.adapter

import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.BASE_URL
import ru.netology.nmedia.databinding.CardAdBinding
import ru.netology.nmedia.handler.loadContentImage
import ru.netology.nmedia.model.Ad

class AdViewHolder (
    private val binding: CardAdBinding,
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(ad: Ad) {
            binding.adImage.loadContentImage("$BASE_URL/media/${ad.image}")
        }
    }