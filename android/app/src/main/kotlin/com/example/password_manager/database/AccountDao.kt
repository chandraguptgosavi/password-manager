package com.example.password_manager.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AccountDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(account: Account): Long

    @Query("SELECT * FROM user_accounts")
    fun getAllAccounts(): List<Account>

    @Delete
    fun delete(account: Account)
}