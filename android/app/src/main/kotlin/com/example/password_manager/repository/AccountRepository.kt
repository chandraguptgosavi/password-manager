package com.example.password_manager.repository

import android.util.Log
import com.example.password_manager.database.Account
import com.example.password_manager.database.AccountDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AccountRepository(private val databaseDao: AccountDao) {
    suspend fun getAllAccounts(): List<Account> {
        return withContext(Dispatchers.IO){
            databaseDao.getAllAccounts()
        }
    }

    suspend fun insert(account: Account) {
        withContext(Dispatchers.IO){
            databaseDao.insert(account)
        }
    }

    suspend fun delete(account: Account) {
        withContext(Dispatchers.IO){
            databaseDao.delete(account)
        }
    }
}