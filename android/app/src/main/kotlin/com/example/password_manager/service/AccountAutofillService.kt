package com.example.password_manager.service

import android.app.assist.AssistStructure
import android.os.CancellationSignal
import android.service.autofill.*
import android.widget.RemoteViews
import com.example.password_manager.AuthActivity
import com.example.password_manager.database.Account
import com.example.password_manager.database.AccountDatabase
import com.example.password_manager.repository.AccountRepository
import com.example.password_manager.utils.StructureParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList

class AccountAutofillService : AutofillService() {
    private val coroutineJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + coroutineJob)
    private lateinit var accountRepository: AccountRepository
    private lateinit var data: List<Account>


    override fun onCreate() {
        super.onCreate()
        val databaseDao = AccountDatabase.getInstance(applicationContext).accountDao
        accountRepository = AccountRepository(databaseDao)
    }

    override fun onFillRequest(request: FillRequest, cancellationSignal: CancellationSignal, callback: FillCallback) {
        // Get the structure from the request
        val fillContextList: List<FillContext> = request.fillContexts
        val structure: AssistStructure = fillContextList[fillContextList.size - 1].structure

        coroutineScope.launch {
            data = accountRepository.getAllAccounts()
            val structureParser = StructureParser(structure)
            if (data.isNotEmpty()) {
                if(structureParser.usernameAutofillId != null && structureParser.passwordAutofillId != null){
                    val authPresentation = RemoteViews(packageName, android.R.layout.simple_list_item_1)
                    authPresentation.setTextViewText(android.R.id.text1, "Autofill with Password Manager")
                    val fillResponse: FillResponse = FillResponse.Builder()
                            .setAuthentication(
                                    structureParser.autofillIdList.toTypedArray(),
                                    AuthActivity.getAuthIntentSender(
                                            applicationContext,
                                            ArrayList(data),
                                            arrayListOf(structureParser.usernameAutofillId!!, structureParser.passwordAutofillId!!)
                                    ),
                                    authPresentation
                            )
                            .build()
                    callback.onSuccess(fillResponse)
                }else{
                    callback.onSuccess(null)
                }
            }else{
                callback.onFailure("No Data Found")
            }
        }
    }

    override fun onSaveRequest(p0: SaveRequest, p1: SaveCallback) {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineJob.cancel()
    }
}