package com.pukachkosnt.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pukachkosnt.data.entities.ArticleEntity

@Database(entities = [ ArticleEntity::class ], version = 1, exportSchema = true)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao

    companion object {
        const val DB_NAME = "newsdb"
    }
}