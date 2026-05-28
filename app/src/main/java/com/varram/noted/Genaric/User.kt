package com.varram.noted.Genaric

data class User(
    val id: Int,
    val name: String,
    val email: String
)
data class SavedPosts(
    val title:String,
    val discription:String,
    val genrated_on:String
)

