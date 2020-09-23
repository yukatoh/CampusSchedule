package com.katoh.campusschedule.viewmodels

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
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
import java.io.IOException
import java.net.URL

class BookViewModel: ViewModel() {
    // Repository
    private val bookRepository = BookRepository(BookLookupService.booksApi)

    // Live Data of bookData
    private val _bookLiveData: MutableLiveData<BookData> by lazy {
        MutableLiveData<BookData>()
    }
    val bookLiveData: LiveData<BookData>
        get() = _bookLiveData
    lateinit var bookList: BookData

    private fun updateBookData(bookData: BookData) {
        bookList = bookData
        _bookLiveData.postValue(bookData)
    }

    /**
     * Fetch BookData object from JSON by BookContent
     */
    fun fetchBookData(bookContent: BookContent) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val params = HashMap<String, Any>()
                params["q"] = getQueryString(bookContent)
                Log.d("queryParams", params["q"].toString())
                Log.d("networkResult", bookRepository.getBookData(params)?.items?.size.toString())
                // Update live data
                bookRepository.getBookData(params)?.let { bookData ->
                    updateBookData(bookData)
                    // fetchThumbnail(bookData)
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
                bookRepository.getBookData(params)?.let { bookData ->
                    updateBookData(bookData)
                }
            }
        }
    }

    /**
     * Get query strings to fetch BookData
     */
    private fun getQueryString(bookContent: BookContent): String =
        StringBuilder().apply {
            append("intitle:${bookContent.title}")
            if (bookContent.author.isNotBlank())
                append("+inauthor:${bookContent.author}")
            if (bookContent.publisher.isNotBlank())
                append("+inpublisher:${bookContent.publisher}")
        }.toString()

    // List of thumbnail bitmaps
    lateinit var thumbnails: List<Bitmap>

    private fun fetchThumbnail(bookData: BookData) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val bmpList = mutableListOf<Bitmap>()
                    bookData.items.forEach { item ->
                        val url = URL(item.volumeInfo.imageLinks?.thumbnail
                            ?.replace("http://", "https://"))
                        val inputStream = url.openStream()
                        val bmp = BitmapFactory.decodeStream(inputStream)
                        bmpList.add(bmp)
                        inputStream.close()
                    }
                    thumbnails = bmpList
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}