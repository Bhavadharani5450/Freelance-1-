package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.FreeverseDao
import com.example.data.model.*

@Database(
    entities = [
        UserEntity::class,
        ProjectEntity::class,
        ProposalEntity::class,
        CampusGigEntity::class,
        EventEntity::class,
        ServiceEntity::class,
        NotificationEntity::class,
        ChatMessageEntity::class,
        AssessmentResultEntity::class,
        AnnouncementEntity::class,
        TransactionEntity::class,
        ReportComplaintEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun freeverseDao(): FreeverseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "freeverse_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
