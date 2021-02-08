package com.example.password_manager

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.password_manager.database.Account
import com.example.password_manager.database.AccountDatabase
import com.example.password_manager.repository.AccountRepository
import kotlinx.coroutines.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RepositoryTest {
    private val coroutineJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + coroutineJob)
    private lateinit var accountRepository: AccountRepository
    private val account = Account(0L, "Title", "username", "password", "package")

    @Before
    fun createRepository(){
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val accountDao = AccountDatabase.getInstance(context).accountDao
        accountRepository = AccountRepository(accountDao)
    }

    @Test
    fun loadData(){
        coroutineScope.launch {
            val data = accountRepository.getAllAccounts()
            assertThat(data::class.simpleName, `is`("ArrayList"))
        }
    }

    @Test
    fun addData(){
        coroutineScope.launch {
            accountRepository.insert(account)
            val data = accountRepository.getAllAccounts()
            for (i in data){
                assertThat(i.title, `is`("Title"))
            }
        }
    }

    @Test
    fun removeData(){
        coroutineScope.launch {
            val data = accountRepository.getAllAccounts()
            if (data.isNotEmpty()){
                val sizeBeforeRemove = data.size
                accountRepository.delete(data[sizeBeforeRemove - 1])
                val newData = accountRepository.getAllAccounts()
                val sizeAfterRemove = newData.size
                assertThat(sizeBeforeRemove, `is`(sizeAfterRemove + 1))
            }
        }
    }

    @After
    fun dispose(){
        coroutineJob.cancel()
    }
}