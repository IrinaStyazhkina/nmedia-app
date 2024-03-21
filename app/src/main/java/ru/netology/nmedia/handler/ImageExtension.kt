package ru.netology.nmedia.handler

import android.widget.ImageView
import com.bumptech.glide.Glide
import ru.netology.nmedia.R

fun ImageView.loadWithCircleCrop(url: String, timeout: Int = 30_000) {
    Glide.with(this)
        .load(url)
        .circleCrop()
        .timeout(timeout)
        .placeholder(R.drawable.baseline_hourglass_empty_24)
        .error(R.drawable.baseline_error_24)
        .into(this)
}

fun ImageView.loadContentImage(url: String, timeout: Int = 30_000) {
    Glide.with(this)
        .load(url)
        .timeout(timeout)
        .fitCenter()
        .error(R.drawable.baseline_error_24)
        .into(this)
}