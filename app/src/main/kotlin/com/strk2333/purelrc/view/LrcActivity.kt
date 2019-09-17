package com.strk2333.purelrc.view

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.strk2333.purelrc.R
import com.strk2333.purelrc.di.module.NetModule
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_lrc.*
import okhttp3.Request
import org.json.JSONObject
import java.lang.Exception

class LrcActivity : AppCompatActivity() {
    private var id:String? = null
    private var name:String? = null
    private val client = NetModule().provideOkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lrc)

        id = intent.getStringExtra("id").toString()
        name = intent.getStringExtra("name").toString()
        title = name
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
                    }catch (e: Exception) {
                        Log.e("Error", e.toString())
                    }
                }
    }

}
