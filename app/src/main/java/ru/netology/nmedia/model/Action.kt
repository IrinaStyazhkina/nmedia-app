package ru.netology.nmedia.model

enum class Action {
    LIKE,
    NEW_POST,
    UNKNOWN;

    companion object {
        fun lookup(action: String): Action {
            return try {
                Action.valueOf(action)
            } catch (ex: IllegalArgumentException) {
                UNKNOWN
            }
        }
    }
}