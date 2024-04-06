package com.example.philipnews.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.philipnews.models.Article

@Database(entities = [Article::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ArticleDatabase : RoomDatabase() {

    abstract fun articleDao(): ArticleDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: ArticleDatabase? = null

      operator  fun invoke(context: Context): ArticleDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE?: synchronized(this) {
                INSTANCE?:createDatabase(context).also {
                    INSTANCE=it
                }

            }
        }

        private fun createDatabase(context: Context): ArticleDatabase {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                ArticleDatabase::class.java,
                "article_database"
            ).build()
            INSTANCE = instance
            // return instance
           return instance
        }
    }
}