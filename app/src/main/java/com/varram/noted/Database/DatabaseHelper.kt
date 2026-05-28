package com.varram.noted.Database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.varram.noted.Genaric.SavedPosts
import com.varram.noted.Genaric.User

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        companion object {
            private const val DATABASE_NAME = "user_database"
            private const val DATABASE_VERSION = 3

            private const val TABLE_USERS = "users"
            private const val TABLE_SAVED_POST = "saved_posts"
            private const val COLUMN_ID = "id"
            private const val COLUMN_UID = "uid"
            private const val COLUMN_NAME = "name"
            private const val COLUMN_EMAIL = "email"
            private const val COLUMN_PROVIDER = "provider"
            private const val COLUMN_CREATEDAT = "createdAt"
            private const val COLUMN_SAVED_POST_TITLE = "saved_post_title"
            private const val COLUMN_LASTLOGIN = "lastLogin"
            private const val COLUMN_PROFILE_URL = "profilePicUrl"
            private const val COULMN_GENRATED_TEXT = "generated_text"
        }

        override fun onCreate(db: SQLiteDatabase) {
            val createTableQuery = ("CREATE TABLE $TABLE_USERS ("
                    + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "$COLUMN_UID TEXT, "
                    + "$COLUMN_NAME TEXT, "
                    + "$COLUMN_EMAIL TEXT, "
                    + "$COLUMN_PROVIDER TEXT, "
                    + "$COLUMN_CREATEDAT TEXT, "
                    + "$COLUMN_LASTLOGIN TEXT, "
                    + "$COLUMN_PROFILE_URL TEXT)")
            db.execSQL(createTableQuery)

            val createSavedPostTable = ("CREATE TABLE $TABLE_SAVED_POST ("
                    + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "$COLUMN_CREATEDAT TEXT, "
                    + "$COLUMN_SAVED_POST_TITLE TEXT, "
                    + "$COULMN_GENRATED_TEXT TEXT)")
            db.execSQL(createSavedPostTable)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_SAVED_POST")
            onCreate(db)
        }

        fun insertSavedPost(createdAt: String,genratedText:String,inputText:String):Long{
            val db = this.writableDatabase
            val values = ContentValues()
            values.put(COLUMN_CREATEDAT, createdAt)
            values.put(COULMN_GENRATED_TEXT, genratedText)
            values.put(COLUMN_SAVED_POST_TITLE, inputText)
            val id = db.insert(TABLE_SAVED_POST, null, values)
//            db.close()
            return id
        }
    fun getImageAndName(): Pair<String?, String?> {
        var name: String? = null
        var imageUrl: String? = null

        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT $COLUMN_NAME, $COLUMN_PROFILE_URL FROM $TABLE_USERS LIMIT 1",
            null
        )

        if (cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
            imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROFILE_URL))
        }

        cursor.close()
        db.close()

        return Pair(name, imageUrl)
    }
    fun clearAllTables() {
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.delete(TABLE_USERS, null, null)
            db.delete(TABLE_SAVED_POST, null, null)
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error clearing tables: ${e.message}")
        } finally {
            db.endTransaction()
        }
    }

    // Insert user
        fun insertUser(uid:String,name: String, email: String,provider: String,createdAt: String, last_login: String, profile_url: String): Long {
            val db = this.writableDatabase
            val values = ContentValues()
            values.put(COLUMN_UID, uid)
            values.put(COLUMN_NAME, name)
            values.put(COLUMN_EMAIL, email)
            values.put(COLUMN_PROVIDER, provider)
            values.put(COLUMN_CREATEDAT, createdAt)
            values.put(COLUMN_LASTLOGIN, last_login)
            values.put(COLUMN_PROFILE_URL, profile_url)
            val id = db.insert(TABLE_USERS, null, values)
//            db.close()
            return id
        }

        // Get all users
        @SuppressLint("RestrictedApi")
        fun getAllUsers(): List<User> {
            val userList = ArrayList<User>()
            val db = this.readableDatabase
            val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_USERS", null)

            if (cursor.moveToFirst()) {
                do {
                    val user = User(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
                    )
                    userList.add(user)
                } while (cursor.moveToNext())
            }
            cursor.close()
            db.close()
            return userList
        }
        fun fetchSavedPostsFromLocalDB():List<SavedPosts>{
            val saveed_post = ArrayList<SavedPosts>()
            val db = this.readableDatabase
            val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_SAVED_POST", null)
            if (cursor.moveToFirst()) {
                do {
                    val user = SavedPosts(
                        title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SAVED_POST_TITLE)),
                        discription = cursor.getString(cursor.getColumnIndexOrThrow(COULMN_GENRATED_TEXT)),
                        genrated_on = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATEDAT))
                    )
                    saveed_post.add(user)
                } while (cursor.moveToNext())
            }
            cursor.close()
            db.close()
            return saveed_post
        }
        // Delete user
        fun deleteUser(id: Int): Int {
            val db = this.writableDatabase
            val result = db.delete(TABLE_USERS, "$COLUMN_ID=?", arrayOf(id.toString()))
            db.close()
            return result
        }
}