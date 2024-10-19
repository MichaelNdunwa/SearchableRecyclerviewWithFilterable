package com.example.searchablerecyclerviewwithfilterable

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.searchablerecyclerviewwithfilterable.adapters.SearchAdapter
import com.example.searchablerecyclerviewwithfilterable.databinding.ActivityMainBinding
import com.example.searchablerecyclerviewwithfilterable.models.BookModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: SearchAdapter
    private lateinit var bookList: MutableList<BookModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        buildRecyclerView()
    }

    private fun buildRecyclerView() {
        bookList = mutableListOf<BookModel>()
        bookList.add(BookModel(bookName = "Deep Work", bookAuthor = "Cal Newport", bookDescription = getString(R.string.deep_work_description), bookImage = getString(R.string.deep_work_url)))
        bookList.add(BookModel(bookName = "Deep Work", bookAuthor = "Cal Newport", bookDescription = getString(R.string.deep_work_description), bookImage = getString(R.string.deep_work_url)))
        bookList.add(BookModel(bookName = "Deep Work", bookAuthor = "Cal Newport", bookDescription = getString(R.string.deep_work_description), bookImage = getString(R.string.deep_work_url)))
        bookList.add(BookModel(bookName = "Deep Work", bookAuthor = "Cal Newport", bookDescription = getString(R.string.deep_work_description), bookImage = getString(R.string.deep_work_url)))
        bookList.add(BookModel(bookName = "Deep Work", bookAuthor = "Cal Newport", bookDescription = getString(R.string.deep_work_description), bookImage = getString(R.string.deep_work_url)))
        bookList.add(BookModel(bookName = "Deep Work", bookAuthor = "Cal Newport", bookDescription = getString(R.string.deep_work_description), bookImage = getString(R.string.deep_work_url)))
        adapter = SearchAdapter(bookList)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
//        binding.searchView.setupWithSearchBar(binding.searchBar)
    }

}