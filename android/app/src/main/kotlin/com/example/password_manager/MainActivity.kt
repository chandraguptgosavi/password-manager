package com.example.password_manager

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.annotation.NonNull
import com.example.password_manager.database.Account
import com.example.password_manager.database.AccountDatabase
import com.example.password_manager.repository.AccountRepository
import com.example.password_manager.utils.METHOD_CHANNEL
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainActivity : FlutterActivity() {
    private val coroutineJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + coroutineJob)
    private lateinit var accountRepository: AccountRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val databaseDao = AccountDatabase.getInstance(this).accountDao
        accountRepository = AccountRepository(databaseDao)
    }

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, METHOD_CHANNEL).setMethodCallHandler { call, result ->
            when (call.method) {
                "loadAccounts" -> loadAccounts(result)
                "addAccount" -> addAccount(call, result)
                "deleteAccount" -> deleteAccount(call, result)
                "enableDrawOverOtherApps" -> enableDrawOverOtherApps(result)
            }
        }
    }

    private fun enableDrawOverOtherApps(result: MethodChannel.Result) {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
        startActivity(intent)
        result.success(true)
    }

    private fun loadAccounts(result: MethodChannel.Result) {
        coroutineScope.launch {
            val allAccounts = accountRepository.getAllAccounts()
            val list: MutableList<Map<String, Any>> = mutableListOf()
            for (account in allAccounts) {
                val map: MutableMap<String, Any> = mutableMapOf()
                map["id"] = account.id
                map["title"] = account.title!!
                map["username"] = account.username!!
                map["password"] = account.password!!
                map["package_name"] = account.packageName!!
                list.add(map)
            }
            list.let {
                result.success(it)
            }
        }
    }

    private fun deleteAccount(call: MethodCall, result: MethodChannel.Result) {
        coroutineScope.launch {
            val account = Account(call.argument("id")!!, call.argument("title")!!, call.argument("username")!!, call.argument("password")!!, call.argument("package_name")!!)
            accountRepository.delete(account)
            result.success(true)
        }
    }

    private fun addAccount(call: MethodCall, result: MethodChannel.Result) {
        coroutineScope.launch {
            val account = Account(0L, call.argument("title")!!, call.argument("username")!!, call.argument("password")!!, call.argument("package_name")!!)
            accountRepository.insert(account)
            result.success(true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineJob.cancel()
    }
}
