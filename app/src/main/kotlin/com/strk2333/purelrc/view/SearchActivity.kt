package com.strk2333.purelrc.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.TextView
import com.strk2333.purelrc.*
import com.strk2333.purelrc.di.module.NetModule
import com.strk2333.purelrc.utils.*
import com.strk2333.purelrc.utils.LocalShared.getHistory
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_lrc.*
import kotlinx.android.synthetic.main.search.*
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception


class SearchActivity : AppCompatActivity() {
    private var pref: SharedPreferences? = null
    private val client = NetModule().provideOkHttpClient()
    private var searchString = ""
    private val localShared = LocalShared
    private val historyList = ArrayList<View>()
    private val suggestList = ArrayList<View>()
    private val historyMax = 10
    private val suggestMax = 4 // 4 is max in netease music

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search)

        initData()
        initListener()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == searchIntentResult && resultCode == RESULT_OK) {
            val dataReturn = data?.getIntExtra("searchBack", searchIntentResult - 1)
            if (dataReturn == searchIntentResult) {
                resetSearch()
            }
        }
    }

    private fun initData() {
        initItems(historyList, historyMax)
        initItems(suggestList, suggestMax)
        resetSearch()
    }

    private fun resetSearch() {
        pref = getSharedPreferences(sharedSearch, Context.MODE_PRIVATE)
        setHistoryView()
        search_view.text = SpannableStringBuilder()
    }

    private fun initItems(list: ArrayList<View>, count: Int) {
        for (i in 0 until count) {
            val btn = Button(this)
            btn.setAllCaps(false)
            btn.setOnClickListener { _ ->
                val str = btn.text.toString().trim()
                onSearch(str)
            }
            list.add(btn)
        }
    }

    private fun initListener() {
        search_view.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val str = p0.toString()
                searchString = str
                if (str != "")
                    setSuggestView()
                else
                    clearSuggestView()
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

        search_history_clear.setOnClickListener {
            localShared.clearHistory(pref!!)
            setHistoryView()
        }
    }

    private fun onSearch(v: String) {
        if (v == "")
            return

        localShared.saveHistory(pref!!, v)

        val intent = Intent(this, SearchResultActivity::class.java)
        intent.putExtra("searchWords", v)
        startActivityForResult(intent, searchIntentResult)
    }

    private fun setHistoryView() {
        val str = getHistory(pref!!)
        search_history.removeAllViews()
        if (str != null) {
            val arr = str.split(placeHolder)
            for (i in historyMax - 1 downTo 0) {
                if (arr.size > i) {
                    val btn = historyList[i] as Button
                    btn.text = arr[i]
                    search_history.addView(historyList[i])
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun setSuggestView() {
        //使用RxJava处理
        Observable.create(ObservableOnSubscribe<String> { e ->
            //使用okhttp3访问网络
            val builder = Request.Builder()
            val request = builder.url("http://106.54.25.147:3000/search/suggest?keywords=$searchString").get().build()
            val response = client.newCall(request).execute()
            val responseBody = response.body()
            val result = responseBody?.string()
            //这里的.string()只能用一次

            //这里其实形参是String类型,然而实参是String?类型,如果直接传result会报错,在后面加!!即可解决
            //发射(这里是被观察者,被观察者发射事件)
            e.onNext(result!!)
            //上面那句代码可以这样写
            //e.onNext(result as String)
        }).subscribeOn(Schedulers.io())  //io线程  被观察者
                .observeOn(AndroidSchedulers.mainThread())  //主线程 观察者
                .subscribe {
                    //这里接收刚刚被观察者发射的事件
                    //这个response就是io线程发射过来的result
                    response ->
                    try {
                        val json = JSONObject(response)
                        val obj = json.getJSONObject("result").getJSONArray("songs")
                        search_hint.removeAllViews()
                        for (i in suggestMax - 1 downTo 0) {
                            if (obj.length() > i) {
                                val info = obj[i] as JSONObject
                                val btn = suggestList[i] as Button
                                btn.text = info["name"].toString()
                                search_hint.addView(suggestList[i])
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("Error", e.toString())
                    }
                }
    }

    private fun clearSuggestView() {
        search_hint.removeAllViews()
    }
}