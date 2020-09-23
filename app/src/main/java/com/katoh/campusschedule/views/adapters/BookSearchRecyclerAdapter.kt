package com.katoh.campusschedule.views.adapters

import android.R.attr
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.katoh.campusschedule.R
import com.katoh.campusschedule.models.entity.BookContent
import com.katoh.campusschedule.models.entity.BookData
import kotlinx.android.synthetic.main.row_book_search_list.view.*


class BookSearchRecyclerAdapter(
    private val context: Context,
    private val bookData: BookData
) : RecyclerView.Adapter<BookSearchRecyclerAdapter.BookSearchHolder>() {

    class BookSearchHolder(view: View) : RecyclerView.ViewHolder(view) {
        var contentText: TextView = view.book_content
        var date: TextView = view.date
        var language: TextView = view.language
        var category: TextView = view.category
        var description: TextView = view.description

        var thumbnail: ImageView = view.thumbnail
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookSearchHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_book_search_list, parent, false)

        return BookSearchHolder(view)
    }

    override fun getItemCount(): Int = bookData.items.size

    override fun onBindViewHolder(holder: BookSearchHolder, position: Int) {
        val volumeInfo = bookData.items[position].volumeInfo

        // View property <- BookData
        holder.contentText.text = BookContent(
            title = volumeInfo.title ?: "",
            author = volumeInfo.authors?.joinToString(",") ?: "",
            publisher = volumeInfo.publisher ?: ""
        ).joinToString()

        holder.date.text = volumeInfo.publishedDate
            ?: context.getString(R.string.message_unknown_date)

        holder.language.text = volumeInfo.language ?: ""

        holder.category.text = volumeInfo.categories?.joinToString(",", "#")

        holder.description.text = volumeInfo.description
            ?: context.getString(R.string.message_missing_description)

        val imageUrlString = volumeInfo.imageLinks?.thumbnail
            ?.replace("http://", "https://")
        Glide.with(context).load(imageUrlString)
            .error(
                Glide.with(context).load(
                    BitmapFactory.decodeResource(context.resources, R.drawable.no_image)
                )
            )
            .listener(createLoggerListener("position:$position"))
            .into(holder.thumbnail)

        holder.itemView.setBackgroundResource(R.drawable.state_list)

        // Set Event Listener
        holder.itemView.setOnClickListener {

        }
    }

    private fun createLoggerListener(name: String): RequestListener<Drawable> =
        object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                if (resource is BitmapDrawable) {
                    resource.bitmap.run {
                        Log.d("GlideApp",
                            "Ready %s bitmap (%,d bytes, size: %d x %d)"
                                .format(name, byteCount, width, height)
                        )
                    }
                }
                return false
            }

        }

}