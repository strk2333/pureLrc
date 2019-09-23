package com.strk2333.purelrc.utils

import android.content.SharedPreferences
import android.util.Log

object LocalShared {
    const val historyMax = 10
    const val suggestMax = 4 // 4 is max in netease music
    const val recordMax = 5
    const val favoriteMax = 10

    fun saveHistory(pref: SharedPreferences, v: String) {
        saveData(pref, v, sharedSearchHistory, historyMax)
    }

    fun clearHistory(pref: SharedPreferences) {
        clearData(pref, sharedSearchHistory)
    }

    fun getHistory(pref: SharedPreferences): String? {
        return pref.getString(sharedSearchHistory, null)
    }

    fun saveRecord(pref: SharedPreferences, v: String) {
        saveData(pref, v, sharedSearchRecord, recordMax)
    }

    fun clearRecord(pref: SharedPreferences) {
        clearData(pref, sharedSearchRecord)
    }

    fun getRecord(pref: SharedPreferences): String? {
        return pref.getString(sharedSearchRecord, null)
    }

    fun saveFavorite(pref: SharedPreferences, v: String) {
        saveData(pref, v, sharedSearchFavorite, favoriteMax)
    }

    fun clearFavorite(pref: SharedPreferences) {
        clearData(pref, sharedSearchFavorite)
    }

    fun cancelFavorite(pref: SharedPreferences, v: String) {
        var str: String = pref.getString(sharedSearchFavorite, null) ?: return

        val list = str.split(placeHolder).toMutableList()
        list -= v
        str = list.joinToString(placeHolder)

        val editor = pref.edit()
        editor?.putString(sharedSearchFavorite, str)
        editor?.apply()
    }

    fun getFavorite(pref: SharedPreferences): String? {
        return pref.getString(sharedSearchFavorite, null)
    }

    private fun saveData(pref: SharedPreferences, v: String, mark: String, max: Int) {
        var str = pref.getString(mark, null)
        if (str != null) {
            val list = str.split(placeHolder).toMutableList()
            val index = list.indexOf(v)
            if (index != -1) {
                list -= v
            }
            if (list.size > max - 1) {
                list.removeAt(0)
            }
            list += v
            str = list.joinToString(placeHolder)
        } else {
            str = v
        }

        val editor = pref.edit()
        editor?.putString(mark, str)
        editor?.apply()
    }

    private fun clearData(pref: SharedPreferences, mark: String) {
        val editor = pref.edit()
        editor?.remove(mark)
        editor?.apply()
    }
}