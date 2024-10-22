package com.example.searchablerecyclerviewwithfilterable

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
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

        buildRecyclerView()
        customHideKeyboard()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun customHideKeyboard() {
       binding.main.setOnTouchListener { v, event ->
           if (event.action == MotionEvent.ACTION_DOWN) {
               val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
               imm.hideSoftInputFromWindow(v.windowToken, 0)
           }
           false
       }
    }

    private fun buildRecyclerView() {
        // initializing bookList
        bookList = mutableListOf<BookModel>()
        adapter = SearchAdapter(bookList)

        // Fetching books from firebase:
        bookDataStore.collection("books")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException? ) {
                    if (error != null) {
                        Log.e("MainActivity", "Error getting documents: ", error)
                        return
                    }

                    if (value != null) {
                        for (dc: DocumentChange in value.documentChanges) {
                            if (dc.type == DocumentChange.Type.ADDED) {
                                bookList.add(dc.document.toObject(BookModel::class.java))
                            }
                        }
                        adapter.notifyDataSetChanged()
                    }
                    binding.recyclerView.adapter = adapter
                    binding.recyclerView.layoutManager = GridLayoutManager(this@MainActivity, 2)
                    binding.recyclerView.setHasFixedSize(false)
                }

            })

        // Implementing search bar
        binding.searchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int ) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int ) {
                adapter.filterList(
                    bookList.filter {
                        it.bookName.contains(s.toString(), ignoreCase = true)
                    }.toMutableList()
                )
            }

            override fun afterTextChanged(s: Editable?) { }

        })
    }

}