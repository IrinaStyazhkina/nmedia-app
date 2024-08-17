package ru.netology.nmedia.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {

    private const val DAY_IN_MILLIS = 24 * 3600000
    private const val TWO_DAYS_IN_MILLIS = 48 * 3600000

    fun isToday(dayInMillis: Long): Boolean {
        val current = System.currentTimeMillis()
        val diff = current - (dayInMillis * 1000)
        return diff < DAY_IN_MILLIS
    }
    fun isYesterday(dayInMillis: Long): Boolean {
        val current = System.currentTimeMillis()
        val diff = current - (dayInMillis * 1000)
        return diff in DAY_IN_MILLIS..<TWO_DAYS_IN_MILLIS
    }

    fun isLastWeek(dayInMillis: Long): Boolean {
        val current = System.currentTimeMillis()
        val diff = current - (dayInMillis * 1000)
        return diff > TWO_DAYS_IN_MILLIS
    }

    fun getDate(timestamp: Long): String {
        val pattern = "yyyy-MM-dd"
        val simpleDateFormat = SimpleDateFormat(pattern, Locale.US)
        return simpleDateFormat.format(Date(timestamp * 1000))
    }
}