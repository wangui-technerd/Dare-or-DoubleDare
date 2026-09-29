package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.DareCardDao
import com.example.data.dao.DareLogDao
import com.example.data.dao.GameSessionDao
import com.example.data.dao.HeartReactionDao
import com.example.data.dao.MutedCardDao
import com.example.data.dao.UserDao
import com.example.data.model.DareCardEntity
import com.example.data.model.DareLogEntity
import com.example.data.model.GameSessionEntity
import com.example.data.model.HeartReactionEntity
import com.example.data.model.MutedCardEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        GameSessionEntity::class,
        DareCardEntity::class,
        DareLogEntity::class,
        MutedCardEntity::class,
        HeartReactionEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun gameSessionDao(): GameSessionDao
    abstract fun dareCardDao(): DareCardDao
    abstract fun dareLogDao(): DareLogDao
    abstract fun mutedCardDao(): MutedCardDao
    abstract fun heartReactionDao(): HeartReactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS muted_cards (
                        userId INTEGER NOT NULL,
                        cardId INTEGER NOT NULL,
                        mutedAt INTEGER NOT NULL,
                        PRIMARY KEY(userId, cardId)
                    )
                """)
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS heart_reactions (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        dareLogId INTEGER NOT NULL,
                        gameSessionId INTEGER NOT NULL,
                        timestamp INTEGER NOT NULL
                    )
                """)
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE users_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        username TEXT NOT NULL,
                        displayName TEXT NOT NULL,
                        avatarEmoji TEXT NOT NULL DEFAULT '💋',
                        avatarColorHex TEXT NOT NULL DEFAULT '#C92A45',
                        safeWord TEXT NOT NULL DEFAULT 'Pineapple',
                        intensityPreference INTEGER NOT NULL DEFAULT 2,
                        gamesPlayed INTEGER NOT NULL DEFAULT 0,
                        daresCompleted INTEGER NOT NULL DEFAULT 0,
                        totalPoints INTEGER NOT NULL DEFAULT 0,
                        createdAt INTEGER NOT NULL
                    )
                """)
                db.execSQL("""
                    INSERT INTO users_new (id, username, displayName, avatarEmoji, avatarColorHex, safeWord, intensityPreference, gamesPlayed, daresCompleted, totalPoints, createdAt)
                    SELECT id, username, displayName, avatarEmoji, avatarColorHex, safeWord, intensityPreference, gamesPlayed, daresCompleted, totalPoints, createdAt FROM users
                """)
                db.execSQL("DROP TABLE users")
                db.execSQL("ALTER TABLE users_new RENAME TO users")
                db.execSQL("CREATE UNIQUE INDEX index_users_username ON users(username)")
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dare_double_dare.db"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                .addCallback(object : Callback() {
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
