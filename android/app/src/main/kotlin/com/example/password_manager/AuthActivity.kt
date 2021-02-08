package com.example.password_manager

import android.app.PendingIntent
import android.app.assist.AssistStructure
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.service.autofill.Dataset
import android.service.autofill.FillResponse
import android.view.View
import android.view.autofill.AutofillId
import android.view.autofill.AutofillManager.EXTRA_ASSIST_STRUCTURE
import android.view.autofill.AutofillManager.EXTRA_AUTHENTICATION_RESULT
import android.view.autofill.AutofillValue
import android.widget.EditText
import android.widget.RemoteViews
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.password_manager.R
import com.example.password_manager.database.Account
import com.example.password_manager.service.AutofillAlertWindowService
import com.example.password_manager.utils.*


class AuthActivity : AppCompatActivity() {
    private var replyIntent: Intent? = null
    private var masterPassword: EditText? = null
    private lateinit var timer: CountDownTimer
    private var windowAlertIntent: Intent? = null

    companion object {
        fun getAuthIntentSender(context: Context, data: ArrayList<Account>, autofillIds: ArrayList<AutofillId>): IntentSender {
            val intent = Intent(context, AuthActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelableArrayList(BUNDLE_PARCELABLE_ACCOUNTS_KEY, data)
            bundle.putParcelableArrayList(BUNDLE_PARCELABLE_IDS_KEY, autofillIds)
            intent.putExtra(INTENT_DATA_KEY, bundle)
            return PendingIntent.getActivity(context, 0, intent,
                    PendingIntent.FLAG_CANCEL_CURRENT).intentSender
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        masterPassword = findViewById(R.id.master_password)
        findViewById<View>(R.id.login).setOnClickListener { login() }
        findViewById<View>(R.id.cancel).setOnClickListener {
            onFailure()
            finish()
        }
        windowAlertIntent = Intent(applicationContext, AutofillAlertWindowService::class.java)
    }

    private fun login() {
        val password = masterPassword?.text?.trim() ?: ""
        val prefs: SharedPreferences = getSharedPreferences(FLUTTER_SHARED_PREFERENCES, MODE_PRIVATE)
        val correctPassword = prefs.getLong("flutter.$MASTER_CODE_KEY", Long.MIN_VALUE)
        if (password.toString().toLong() == correctPassword) {
            onSuccess()
        } else {
            Toast.makeText(this, "Incorrect Password", Toast.LENGTH_SHORT).show()
            onFailure()
        }
    }

    private fun onSuccess() {
        val intent = intent
        val bundle: Bundle? = intent.getBundleExtra(INTENT_DATA_KEY)
        val data: ArrayList<Account>? = bundle?.getParcelableArrayList(BUNDLE_PARCELABLE_ACCOUNTS_KEY)
        val autofillIds: ArrayList<AutofillId>? = bundle?.getParcelableArrayList(BUNDLE_PARCELABLE_IDS_KEY)
        val structure: AssistStructure? = intent.getParcelableExtra(EXTRA_ASSIST_STRUCTURE)
        replyIntent = Intent()
        if (structure != null) {
            val clientPackage = structure.activityComponent.packageName
            val account = data?.find {
                clientPackage == it.packageName
            }
            if (autofillIds?.size == 2) {
                if (account != null) {
                    val usernamePresentation = RemoteViews(packageName, android.R.layout.simple_list_item_1)
                    usernamePresentation.setTextViewText(android.R.id.text1, "Username for ${account.title}")
                    val passwordPresentation = RemoteViews(packageName, android.R.layout.simple_list_item_1)
                    passwordPresentation.setTextViewText(android.R.id.text1, "Password for ${account.title}")
                    val fillResponse: FillResponse = FillResponse.Builder()
                            .addDataset(
                                    Dataset.Builder()
                                            .setValue(
                                                    autofillIds[0],
                                                    AutofillValue.forText(account.username),
                                                    usernamePresentation
                                            )
                                            .setValue(
                                                    autofillIds[1],
                                                    AutofillValue.forText(account.password),
                                                    passwordPresentation
                                            )
                                            .build()
                            )
                            .build()
                    setResponseIntent(fillResponse)
                    startTimer()
                    finish()
                } else {
                    if (data != null) {
                        val fillResponseBuilder: FillResponse.Builder = FillResponse.Builder()
                        for (iteratingAccount in data) {
                            val usernamePresentation = RemoteViews(packageName, android.R.layout.simple_list_item_1)
                            usernamePresentation.setTextViewText(android.R.id.text1, "Username for ${iteratingAccount.title}")
                            val passwordPresentation = RemoteViews(packageName, android.R.layout.simple_list_item_1)
                            passwordPresentation.setTextViewText(android.R.id.text1, "Password for ${iteratingAccount.title}")
                            fillResponseBuilder.addDataset(
                                    Dataset.Builder()
                                            .setValue(
                                                    autofillIds[0],
                                                    AutofillValue.forText(iteratingAccount.username),
                                                    usernamePresentation
                                            )
                                            .setValue(
                                                    autofillIds[1],
                                                    AutofillValue.forText(iteratingAccount.password),
                                                    passwordPresentation
                                            )
                                            .build()
                            )
                        }
                        val fillResponse = fillResponseBuilder.build()
                        setResponseIntent(fillResponse)
                        startTimer()
                        finish()
                    } else {
                        onFailure()
                        finish()
                        Toast.makeText(applicationContext, "Unable to Autofill", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                onFailure()
                finish()
                Toast.makeText(applicationContext, "Unable to Autofill", Toast.LENGTH_SHORT).show()
            }
        } else {
            onFailure()
            finish()
            Toast.makeText(applicationContext, "Unable to Autofill", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setResponseIntent(fillResponse: FillResponse) {
        replyIntent?.putExtra(EXTRA_AUTHENTICATION_RESULT, fillResponse)
    }

    private fun startTimer() {
        if (Settings.canDrawOverlays(applicationContext)) {
            timer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND) {
                override fun onTick(p0: Long) {
                    // Nothing TO-DO
                }

                override fun onFinish() {
                    stopService(windowAlertIntent)
                }
            }
            timer.start()
            startService(windowAlertIntent)
        }
    }

    override fun finish() {
        if (replyIntent != null) {
            setResult(RESULT_OK, replyIntent)
        } else {
            setResult(RESULT_CANCELED)
        }
        super.finish()
    }

    private fun onFailure() {
        replyIntent = null
    }
}
