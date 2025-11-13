package com.omkar.noted.Adapters

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.omkar.noted.Genaric.SavedPosts
import com.omkar.noted.R


class SavedPostAdapter(private val itemList: List<SavedPosts>) : RecyclerView.Adapter<SavedPostAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val savedPostsTitle : TextView = itemView.findViewById(R.id.saveed_post_title)
        val savedPostsDes: TextView = itemView.findViewById(R.id.saved_poast_description)
        val genratedOn: TextView = itemView.findViewById(R.id.saved_poast_genrated_on)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.saved_post_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = itemList[position]
        holder.savedPostsTitle.text = item.title
        holder.genratedOn.text = "Genrated On :"+item.genrated_on
        holder.savedPostsDes.text = item.discription
    }

    override fun getItemCount(): Int = itemList.size
}