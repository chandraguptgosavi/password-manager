package com.example.password_manager.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Account::class], version = 1, exportSchema = false)
abstract class AccountDatabase: RoomDatabase() {
    abstract val accountDao: AccountDao

    companion object{
        @Volatile
        private var INSTANCE : AccountDatabase? = null

        fun getInstance(context: Context): AccountDatabase {
            synchronized(this){
                var instance = INSTANCE
                if (instance==null){
                    instance = Room.databaseBuilder(
                            context.applicationContext,
                            AccountDatabase::class.java,
                            "user_accounts_database"
                    )
                            .fallbackToDestructiveMigration()
                            .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}