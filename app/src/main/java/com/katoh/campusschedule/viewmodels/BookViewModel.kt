package com.katoh.campusschedule.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.katoh.campusschedule.models.entity.BookContent
import com.katoh.campusschedule.models.entity.BookData
import com.katoh.campusschedule.models.repository.BookRepository
import com.katoh.campusschedule.services.BookLookupService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BookViewModel: ViewModel() {
    // Repository
    private val repository = BookRepository(BookLookupService.booksApi)

    // Live Data
    private val _bookList: MutableLiveData<BookData> by lazy {
        MutableLiveData<BookData>()
    }
    val bookList: LiveData<BookData>
        get() = _bookList

    /**
     * Fetch BookData object from JSON by BookContent
     */
    fun fetchBookData(bookContent: BookContent) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val params = HashMap<String, Any>()
                params["q"] = """
                    intitle:${bookContent.title}+
                    inauthor:${bookContent.author}+
                    inpublisher:${bookContent.publisher}
                    """.trimIndent()

                // Update live data
                repository.getBookData(params)?.let { bookData ->
                    _bookList.postValue(bookData)
                }
            }
        }
    }

    /**
     * Fetch BookData object from JSON by keyword strings
     */
    fun fetchBookData(vararg keywords: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val params = HashMap<String, Any>()
                params["q"] = keywords.joinToString("+")

                // Update live data
                repository.getBookData(params)?.let { bookData ->
                    _bookList.postValue(bookData)
                }
            }
        }
    }

}