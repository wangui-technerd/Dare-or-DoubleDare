package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.DareCardDao
import com.example.data.dao.DareLogDao
import com.example.data.dao.GameSessionDao
import com.example.data.dao.UserDao
import com.example.data.model.DareCardEntity
import com.example.data.model.DareLogEntity
import com.example.data.model.GameSessionEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        GameSessionEntity::class,
        DareCardEntity::class,
        DareLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun gameSessionDao(): GameSessionDao
    abstract fun dareCardDao(): DareCardDao
    abstract fun dareLogDao(): DareLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dare_double_dare.db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            getInstance(context).prepopulateCards()
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }
    }

    suspend fun prepopulateCards() {
        val defaultCount = dareCardDao().getDefaultCardCount()
        if (defaultCount < DefaultCards.CARDS.size) {
            dareCardDao().insertCards(DefaultCards.CARDS)
        }
    }
}
