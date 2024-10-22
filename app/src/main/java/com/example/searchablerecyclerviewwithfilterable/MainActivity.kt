package com.example.searchablerecyclerviewwithfilterable

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.searchablerecyclerviewwithfilterable.adapters.SearchAdapter
import com.example.searchablerecyclerviewwithfilterable.databinding.ActivityMainBinding
import com.example.searchablerecyclerviewwithfilterable.models.BookModel
import com.google.android.material.internal.ViewUtils.hideKeyboard
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: SearchAdapter
    private lateinit var bookList: MutableList<BookModel>
    private lateinit var bookDataStore: FirebaseFirestore
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

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
        // Initialize FirebaseApp and Firestore:
        FirebaseApp.initializeApp(this)
        bookDataStore = FirebaseFirestore.getInstance()

        // initialize swipeRefreshLayout
        swipeRefreshLayout = binding.swipeRefreshLayout

        buildRecyclerView() // build recycler view

        // pull to refresh:
        refreshBooks()
    }

    // I override the dispatchTouchEvent to enable me hide keyboard when the other view is clicked:
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev!!.action == MotionEvent.ACTION_DOWN) {
            customHideKeyboard()
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun refreshBooks() {
        swipeRefreshLayout.setOnRefreshListener {
            Toast.makeText(this@MainActivity, "Book list updated", Toast.LENGTH_SHORT).show()
            buildRecyclerView()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun customHideKeyboard() {
        if (this.currentFocus != null) {
            val imm = this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(this.currentFocus!!.windowToken, 0)
        }
    }

    private fun buildRecyclerView() {
        // Show progress bar:
        binding.progressBar.visibility = View.VISIBLE

        // initializing bookList
        bookList = mutableListOf<BookModel>()
        adapter = SearchAdapter(bookList)

        // Fetching books from firebase:
        bookDataStore.collection("books").get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot != null) {
                    for (document in querySnapshot.documents) {
                        bookList.add(document.toObject(BookModel::class.java)!!)
                    }
                    adapter.notifyDataSetChanged()
                    binding.recyclerView.apply {
                        adapter = this@MainActivity.adapter
                        layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
                        setHasFixedSize(false)
                    }
                }
                // Hide progress bar:
                binding.progressBar.visibility = View.GONE
            }
            .addOnFailureListener { exception ->
                Log.e("MainActivity", "Error getting documents: ", exception)
                Toast.makeText(this@MainActivity, "Error getting documents: $exception", Toast.LENGTH_SHORT).show()

                // Hide progress bar:
                binding.progressBar.visibility = View.GONE
            }

        // Implementing search bar
        binding.searchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int ) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int ) {
                adapter.filterList(
                    bookList.filter {
                        it.bookName.contains(s.toString(), ignoreCase = true) ||
                                it.bookAuthor.contains(s.toString(), ignoreCase = true)
                    }.toMutableList()
                )
            }

            override fun afterTextChanged(s: Editable?) { }

        })
    }

}