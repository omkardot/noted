package com.omkar.noted.Fragements

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.omkar.noted.Adapters.SavedPostAdapter
import com.omkar.noted.Database.DatabaseHelper
import com.omkar.noted.R

class SavedPostFragment : Fragment() {
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view= inflater.inflate(R.layout.fragment_saved_post, container, false)
        dbHelper = DatabaseHelper(view.context)
        val recyclerView = view.findViewById<RecyclerView>(R.id.post_recycleview)
        val savedPosts=dbHelper.fetchSavedPostsFromLocalDB()
        Log.d("savedPosts",savedPosts.toString())

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = SavedPostAdapter(savedPosts)

        return view
    }
}