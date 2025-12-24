package com.omkar.noted.Fragements

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.omkar.noted.Adapters.SavedPostAdapter
import com.omkar.noted.Database.DatabaseHelper
import com.omkar.noted.R

class SavedPostFragment : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_saved_post)
        dbHelper = DatabaseHelper(this@SavedPostFragment)
        val recyclerView = findViewById<RecyclerView>(R.id.post_recycleview)
        val actio_back = findViewById<ImageView>(R.id.action_back)
        val savedPosts=dbHelper.fetchSavedPostsFromLocalDB()
        Log.d("savedPosts",savedPosts.toString())
        actio_back.setOnClickListener {
           onBackPressed()
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = SavedPostAdapter(savedPosts)

    }
}