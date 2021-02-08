package com.example.password_manager.utils

import android.app.assist.AssistStructure
import android.view.View
import android.view.autofill.AutofillId
import java.util.*

class StructureParser(private val structure: AssistStructure) {
    val autofillIdList = mutableListOf<AutofillId>()
    var usernameAutofillId: AutofillId? = null
    var passwordAutofillId: AutofillId? = null

    init {
        traverseStructure()
    }

    private fun traverseStructure(){
        val windowNodesCount: Int = structure.windowNodeCount
        for (i in 0 until windowNodesCount) {
            val windowNode: AssistStructure.WindowNode = structure.getWindowNodeAt(i)
            val viewNode: AssistStructure.ViewNode = windowNode.rootViewNode
            traverseNode(viewNode)
        }
    }

    private fun traverseNode(viewNode: AssistStructure.ViewNode?) {
        if (viewNode?.autofillType == View.AUTOFILL_TYPE_TEXT) {
            viewNode.autofillId?.let { autofillIdList.add(it) }
            if (viewNode.autofillHints?.isNotEmpty() == true) {
                viewNode.autofillHints?.map {
                    setAutofillIds(it.toLowerCase(Locale.ROOT), viewNode)
                }
            } else {
                viewNode.hint?.let {
                    setAutofillIds(it.toLowerCase(Locale.ROOT), viewNode)
                }
            }

        }
        for (i in 0 until viewNode!!.childCount) {
            val childNode: AssistStructure.ViewNode = viewNode.getChildAt(i)
            traverseNode(childNode)
        }
    }

    private fun setAutofillIds(string: String, viewNode: AssistStructure.ViewNode) {
        val condition = string.contains("mobile") || string.contains("phone") || string.contains("email") || string.contains("username")
        if (condition) {
            if (usernameAutofillId == null) {
                usernameAutofillId = viewNode.autofillId
            }
        }
        if (string == "password") {
            if (passwordAutofillId == null) {
                passwordAutofillId = viewNode.autofillId
            }
        }
    }
}