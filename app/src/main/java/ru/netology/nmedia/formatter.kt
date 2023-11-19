package ru.netology.nmedia

fun formatCount(count: Int): String {
    return when(count) {
        in 0 .. 999 -> count.toString()
        in 1000 .. 9999 -> {
            val res = count / 100
            if(res % 10 == 0) {
                return String.format("%dK", res / 10)
            } else {
                String.format("%.1fK", res.toDouble() / 10)
            }
        }
        in 10000 .. 999999 -> String.format("%dK", count / 1000)
        in 1000000..Integer.MAX_VALUE -> {
            val res = count / 100000
            if(res % 10 == 0) {
                return String.format("%dM", res / 10)
            } else {
                String.format("%.1fM", res.toDouble() / 10)
            }
        }
        else -> "NaN"
    }
}