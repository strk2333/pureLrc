package com.strk2333.purelrc.utils

import android.content.SharedPreferences

object LocalShared {
    fun saveHistory(pref: SharedPreferences, v: String) {
        var str = pref.getString(sharedSearchHistory, null)
        if (str != null) {
            val list = str.split(placeHolder).toMutableList()
            val index = list.indexOf(v)
            if (index != -1) {
                list -= v
            }
            if (list.size > 9) {
                list.removeAt(0)
            }
            list += v
            str = list.joinToString(placeHolder)
        } else {
            str = v
        }

        val editor = pref.edit()
        editor?.putString(sharedSearchHistory, str)
        editor?.apply()
    }

    fun clearHistory(pref: SharedPreferences) {
        val editor = pref.edit()
        editor?.remove(sharedSearchHistory)
        editor?.apply()
    }

    fun getHistory(pref: SharedPreferences): String? {
        return pref.getString(sharedSearchHistory, null)
    }
}