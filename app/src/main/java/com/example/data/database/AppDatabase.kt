package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.FreeverseDao
import com.example.data.model.AssessmentResultEntity
import com.example.data.model.CampusGigEntity
import com.example.data.model.ChatMessageEntity
import com.example.data.model.EventEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.ProposalEntity
import com.example.data.model.ServiceEntity
import com.example.data.model.UserEntity

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
        AssessmentResultEntity::class
    ],
    version = 1,
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
