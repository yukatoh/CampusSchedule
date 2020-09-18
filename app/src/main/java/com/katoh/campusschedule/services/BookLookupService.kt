package com.katoh.campusschedule.services

import com.katoh.campusschedule.models.net.BooksApiInterface

object BookLookupService : BaseService() {
    val booksApi: BooksApiInterface = retrofit.create(BooksApiInterface::class.java)
}