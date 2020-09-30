package com.katoh.campusschedule.models.entity

data class BookContent(
    val title: String = "",
    val author: String = "",
    val publisher: String = ""
) {
    fun joinToString(): String {
        return when  {
            title.isBlank() -> ""
            author.isBlank() and publisher.isBlank() -> title
            author.isBlank() -> "$title - $publisher"
            publisher.isBlank() -> "$title - $author"
            else -> "$title - $author - $publisher"
        }
    }
}