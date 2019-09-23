package com.strk2333.purelrc.utils

import android.content.Context
import android.widget.Toast

val sharedSearch = "SEARCH_PREF"
val sharedSearchHistory = "SEARCH_PREF_HISTORY"
val sharedSearchRecord = "SEARCH_PREF_RECORD"
val sharedSearchFavorite = "SEARCH_PREF_FAVORITE"
val placeHolder = "<com.strk2333.puelrc>"
val placeHolder2 = "<com.strk2333.purerc>"
val unreachableValue = "-23f73y3"
val favText = "收藏"
val cancelFavText = "取消收藏"

val searchIntentResult = 1

fun toast(ctx: Context, v: String) {
    Toast.makeText(ctx, v, Toast.LENGTH_SHORT).show()
}
