package com.example.searchablerecyclerviewwithfilterable.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.searchablerecyclerviewwithfilterable.databinding.BookViewHolderBinding
import com.example.searchablerecyclerviewwithfilterable.models.BookModel
import com.example.searchablerecyclerviewwithfilterable.R
import com.squareup.picasso.Picasso

class SearchAdapter(val bookList: MutableList<BookModel>) :
    RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

//    private lateinit var bookList: MutableList<BookModel>

   /* fun filterList(filteredBookList: MutableList<BookModel>) {
        bookList = filteredBookList
        notifyDataSetChanged()
    }*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding =
            BookViewHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        bookList = bookList
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val book = bookList[position]
        holder.binding.apply {
            bookTv.text = book.bookName
            authorTv.text = book.bookAuthor
            descriptionTv.text = book.bookDescription
            Picasso.get().load(book.bookImage).resize(300, 300).placeholder(R.drawable.book_place_holder)
                .error(R.drawable.error_image).into(bookIv)
        }
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    class SearchViewHolder(val binding: BookViewHolderBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }
}