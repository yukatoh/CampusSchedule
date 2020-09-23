package com.katoh.campusschedule.models.entity

data class BookContent(
    val title: String = "",
    val author: String = "",
    val publisher: String = ""
) {
    fun toList(): List<String> = listOf(title, author, publisher)

    fun joinToString(): String {
        return when  {
            title.isBlank() -> ""
            author.isBlank() -> "%s - %s".format(title, publisher)
            publisher.isBlank() -> "%s - %s".format(title, author)
            else -> "%s - %s - %s".format(title, author, publisher)
        }
    }
}