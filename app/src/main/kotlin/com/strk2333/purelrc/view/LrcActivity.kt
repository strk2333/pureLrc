package com.strk2333.purelrc.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.strk2333.purelrc.R
import com.strk2333.purelrc.di.module.NetModule
import com.strk2333.purelrc.utils.*
import com.strk2333.purelrc.utils.LocalShared.getFavorite
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_lrc.*
import okhttp3.Request
import org.json.JSONObject
import java.lang.Exception
import com.strk2333.purelrc.utils.LocalShared.saveFavorite
import android.view.WindowManager


class LrcActivity : AppCompatActivity() {
    private var pref: SharedPreferences? = null
    private var id: String? = null
    private var name: String? = null
    private var isFav: Boolean = false
    private val client = NetModule().provideOkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lrc)

        initData()
        initListener()
        refreshFavorite()
    }

    private fun initData() {
        //After LOLLIPOP not translucent status bar
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        //Then call setStatusBarColor.
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = resources.getColor(com.strk2333.purelrc.R.color.colorPrimary)

        pref = getSharedPreferences(sharedSearch, Context.MODE_PRIVATE)
        id = intent.getStringExtra("id").toString()
        name = intent.getStringExtra("name").toString()
        lrc_title.text = name
        getLrc()
    }

    @SuppressLint("CheckResult")
    private fun getLrc() {
        //使用RxJava处理
        Observable.create(ObservableOnSubscribe<String> { e ->
            //使用okhttp3访问网络
            val builder = Request.Builder()
            val request = builder.url("http://106.54.25.147:3000/lyric?id=$id").get().build()
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
                        val obj = json.getJSONObject("lrc").get("lyric")
                        lrc.text = Regex("\\[.*\\]").replace(obj.toString(), "").trim()
                    } catch (e: Exception) {
                        Log.e("Error", e.toString())
                    }
                }
    }

    private fun initListener() {
        lrc_favorite.setOnClickListener { _ ->
            val text = name + placeHolder2 + id
            if (isFav) {
                cancelFavorite(text)
                lrc_favorite.text = favText
            } else {
                favorite(text)
                lrc_favorite.text = cancelFavText
            }
        }
    }

    private fun favorite(v: String) {
        saveFavorite(pref!!, v)
    }

    private fun cancelFavorite(v: String) {
        LocalShared.cancelFavorite(pref!!, v)
    }

    private fun refreshFavorite() {
        val fav = getFavorite(pref!!)
        var favArr: List<String>? = null
        if (!fav.isNullOrEmpty()) {
            favArr = fav.split(placeHolder).map { v ->
                val arr = v.split(placeHolder2)
                if (arr.size == 2) arr[1] else unreachableValue
            }
            isFav = favArr.contains(id)
        }
        lrc_favorite.text = if (isFav) cancelFavText else favText
    }
}
