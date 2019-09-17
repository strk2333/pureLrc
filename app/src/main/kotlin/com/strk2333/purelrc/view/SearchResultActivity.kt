package com.strk2333.purelrc.view

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
import kotlinx.android.synthetic.main.search_res.*
import android.content.Intent
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.strk2333.purelrc.*
import com.strk2333.purelrc.components.MyAdapter
import com.strk2333.purelrc.di.module.NetModule
import com.strk2333.purelrc.utils.LocalShared.saveHistory
import com.strk2333.purelrc.utils.searchIntentResult
import com.strk2333.purelrc.utils.sharedSearch
import com.strk2333.purelrc.utils.toast
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.Request
import org.json.JSONObject
import java.lang.Exception


class SearchResultActivity : AppCompatActivity() {
    private val client = NetModule().provideOkHttpClient()
    private var searchString = ""
    private var pref: SharedPreferences? = null
    private var mAdapter: MyAdapter? = null
    private var listData = ArrayList<Map<String, String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_res)
        initData()
        initListener()
        searchString = intent.getStringExtra("searchWords").toString()
        search_res_view.text = SpannableStringBuilder(searchString)
        search_res_view.setSelection(searchString.length)
        fetch(searchString)
//        val rxBus = RxBus()
//        rxBus.toObservable()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(Consumer {
//                    val req = Request.Builder()
//                            .url("http://http://106.54.25.147:3000/search?keywords=%E6%B5%B7%E9%98%94%E5%A4%A9%E7%A9%BA")
//                            .build()
//                    val res = NetModule().provideOkHttpClient().newCall(req).execute()
//                    resData = res.body().toString()
//                    search_res_test.text = resData
//
//                })
    }

    private fun initData() {
        pref = getSharedPreferences(sharedSearch, Context.MODE_PRIVATE)
        mAdapter = MyAdapter(listData)
        mAdapter!!.setClickCallBack(object : MyAdapter.ItemClickCallBack {
            override fun onItemClick(pos: Int) {
                searchLrc(listData[pos]["id"] ?: error("id not found"),
                        listData[pos]["name"] ?: error("id not found"))
            }
        })
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        search_res_swipe.layoutManager = layoutManager
        search_res_swipe.adapter = mAdapter
        search_res_swipe.setPullRefreshEnabled(false)
        search_res_swipe.setLoadingMoreEnabled(true)
    }

    private fun initListener() {
        search_res_view.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val str = p0.toString()
                searchString = str
            }
        })

        search_res_view.setOnEditorActionListener(object : TextView.OnEditorActionListener {
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
        if (v == "")
            return
        saveHistory(pref!!, v)
        fetch(v)
    }

    @SuppressLint("CheckResult")
    private fun fetch(v: String) {
        //使用RxJava处理
        Observable.create(ObservableOnSubscribe<String> { e ->
            //使用okhttp3访问网络
            val builder = Request.Builder()
            val request = builder.url("http://106.54.25.147:3000/search?keywords=$v").get().build()
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
                        listData.clear()
                        for (i in 0 until obj.length()) {
                            val info = obj[i] as JSONObject
                            val map = mutableMapOf<String, String>()
                            map["name"] = info["name"].toString()
                            map["id"] = info["id"].toString()
                            listData.add(map)
                        }
                        Log.i("res", listData.toString())
                        mAdapter?.notifyDataSetChanged()
                    } catch (e: Exception) {
                        Log.e("Error", e.toString())
                    }
                }
    }

    fun searchLrc(id: String, name: String) {
        val intent = Intent(this, LrcActivity::class.java)
        intent.putExtra("id", id)
        intent.putExtra("name", name)
        startActivityForResult(intent, searchIntentResult)
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("searchBack", searchIntentResult)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
