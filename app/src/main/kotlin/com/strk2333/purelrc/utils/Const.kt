package com.strk2333.purelrc.utils

import android.content.Context
import android.widget.Toast

val sharedSearch = "SEARCH_PREF"
val sharedSearchHistory = "SEARCH_PREF_HISTORY"
val placeHolder = "<com.strk2333.puelrc>"

val searchIntentResult = 1

fun toast(ctx: Context, v: String) {
    Toast.makeText(ctx, v, Toast.LENGTH_SHORT).show()
}
