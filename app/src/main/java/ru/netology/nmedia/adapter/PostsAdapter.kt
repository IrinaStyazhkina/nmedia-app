package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.OnInteractionListener
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardAdBinding
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.databinding.CardSeparatorBinding
import ru.netology.nmedia.model.Ad
import ru.netology.nmedia.model.FeedItem
import ru.netology.nmedia.model.TimeSeparator

class PostsAdapter(private val onInteractionListener: OnInteractionListener) : PagingDataAdapter<FeedItem, RecyclerView.ViewHolder>(PostDiffCallback()) {

    override fun getItemViewType(position: Int): Int =
        when(getItem(position)){
            is Ad -> R.layout.card_ad
            is Post -> R.layout.card_post
            is TimeSeparator -> R.layout.card_separator
            null -> error("Unknown item type")
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder  =
        when(viewType) {
            R.layout.card_post -> {
                val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PostViewHolder(binding, onInteractionListener)
            }
            R.layout.card_ad-> {
                val binding = CardAdBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AdViewHolder(binding)
            }
            R.layout.card_separator -> {
                val binding = CardSeparatorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                SeparatorViewHolder(binding)
            }
            else -> error("Unknown view type $viewType")
        }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       when(val item = getItem(position)) {
           is Ad -> (holder as? AdViewHolder)?.bind(item)
           is Post -> (holder as? PostViewHolder)?.bind(item)
           is TimeSeparator -> (holder as? SeparatorViewHolder)?.bind(item)
           null -> error("Unknown item type")
       }
    }
}