package com.strk2333.purelrc.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.strk2333.purelrc.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.search.*


class SearchActivity : AppCompatActivity() {
    private var pref: SharedPreferences? = null
    private var searchString = ""
    private var searchHistory: TextView? = null
    private var infoView: View? = null
    private var resView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search)

        initUI()
        initData()
        initListener()
    }

    private fun initUI() {
        infoView = layoutInflater.inflate(R.layout.search_info, search_container);
        searchHistory = infoView?.findViewById(R.id.search_history) as TextView
    }

    private fun initData() {
        pref = getSharedPreferences(sharedSearch, Context.MODE_PRIVATE)
//        clearHistory()
        setHistory()
    }

    private fun initListener() {
        search_view.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val str = p0.toString()
                searchString = str
            }
        })

        search_view.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
                if (p1 == EditorInfo.IME_ACTION_SEARCH) {
                    onSearch(searchString.trim())
                    return true
                }
                return false
            }
        })
    }

    private fun onSearch(v: String) {
        saveHistory(v)
        setHistory()
        toggleView(true)
    }

    private fun toggleView(showRes: Boolean) {
        if (showRes) {
            search_container.removeAllViewsInLayout()

            resView = layoutInflater.inflate(R.layout.search_res, search_container);
        }
    }

    private fun saveHistory(v: String) {
        if (v == "")
            return

        var str = pref?.getString(sharedSearchHistory, null)
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

        val editor = pref?.edit()
        editor?.putString(sharedSearchHistory, str)
        editor?.apply()
    }

    private fun clearHistory() {
        val editor = pref?.edit()
        editor?.remove(sharedSearchHistory)
        editor?.apply()
    }

    private fun setHistory() {
        val str = pref?.getString(sharedSearchHistory, null)
        if (str != null) {
            var res = ""
            str.split(placeHolder).reversed().forEach { i ->
                res += "$i\n"
            }
            searchHistory?.text = res
        }
    }
}