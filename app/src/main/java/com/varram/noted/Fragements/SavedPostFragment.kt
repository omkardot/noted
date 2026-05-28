package com.varram.noted.Fragements

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.varram.noted.Adapters.SavedPostAdapter
import com.varram.noted.Database.DatabaseHelper
import com.varram.noted.R

class SavedPostFragment : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_saved_post)
        dbHelper = DatabaseHelper(this@SavedPostFragment)
        val recyclerView = findViewById<RecyclerView>(R.id.post_recycleview)
        val no_post_text = findViewById<TextView>(R.id.no_post_text)
        val actio_back = findViewById<ImageView>(R.id.action_back)
        val savedPosts=dbHelper.fetchSavedPostsFromLocalDB()
        Log.d("savedPosts",savedPosts.toString())
        actio_back.setOnClickListener {
           onBackPressed()
        }
        if(savedPosts.isNotEmpty()){
            recyclerView.visibility = View.VISIBLE
            no_post_text.visibility = View.GONE
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = SavedPostAdapter(savedPosts)
        }
        else{
            recyclerView.visibility = View.GONE
            no_post_text.visibility = View.VISIBLE
        }

    }
}