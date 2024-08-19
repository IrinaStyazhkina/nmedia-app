package ru.netology.nmedia.adapter

import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardSeparatorBinding
import ru.netology.nmedia.model.TimeSeparator
import ru.netology.nmedia.model.TimeSeparatorValue

class SeparatorViewHolder (
    private val binding: CardSeparatorBinding,
): RecyclerView.ViewHolder(binding.root) {
    fun bind(separator: TimeSeparator) {
        binding.timeSeparator.setText(getSeparatorText(separator.text))
    }

    private fun getSeparatorText(value: TimeSeparatorValue): Int =
        when(value){
            TimeSeparatorValue.TODAY -> R.string.today
            TimeSeparatorValue.YESTERDAY -> R.string.yesterday
            TimeSeparatorValue.LAST_WEEK -> R.string.last_week
        }
}