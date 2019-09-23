package com.strk2333.purelrc.main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.strk2333.purelrc.R
import com.strk2333.purelrc.Application
import com.strk2333.purelrc.utils.*
import com.strk2333.purelrc.utils.LocalShared.clearFavorite
import com.strk2333.purelrc.utils.LocalShared.clearRecord
import com.strk2333.purelrc.utils.LocalShared.favoriteMax
import com.strk2333.purelrc.utils.LocalShared.getFavorite
import com.strk2333.purelrc.utils.LocalShared.getRecord
import com.strk2333.purelrc.utils.LocalShared.recordMax
import com.strk2333.purelrc.view.LrcActivity
import com.strk2333.purelrc.view.SearchActivity
import com.strk2333.purelrc.view.SearchResultActivity
import kotlinx.android.synthetic.main.acitvity_main.*
import kotlinx.android.synthetic.main.search.*
import org.json.JSONObject
import javax.inject.Inject

class MainActivity : AppCompatActivity(), IMainView {
    private var pref: SharedPreferences? = null
    private val recordList = ArrayList<View>()
    private val favoriteList = ArrayList<View>()

    @Inject
    lateinit var presenter : MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acitvity_main)
        DaggerMainComponent.builder().applicationComponent(Application.component).mainModule(MainModule(this)).build().inject(this)

        initData()
        initListener()
    }

    private fun initData() {
        mainSearch.maxWidth
        pref = getSharedPreferences(sharedSearch, Context.MODE_PRIVATE)
        initItems(recordList, recordMax)
        initItems(favoriteList, favoriteMax)
        refreshRecord()
        refreshFavorite()
    }

    private fun initItems(list: ArrayList<View>, count: Int) {
        for (i in 0 until count) {
            val btn = Button(this)
            btn.setAllCaps(false)
            btn.setOnClickListener { _ ->
                val str = btn.text.toString().trim()
                onSearch(str, btn.id.toString())
            }
            list.add(btn)
        }
    }

    private fun initListener() {
        mainSearch.setOnClickListener {
            val i = Intent(this, SearchActivity::class.java)
            startActivity(i)
        }

        mainFab.setOnClickListener{
            toast(this, "不要瞎点")
        }
    }

    override fun onResume() {
        super.onResume()
        refreshRecord()
        refreshFavorite()
    }

    private fun onSearch(name: String, id: String) {
        if (name == "")
            return

        val intent = Intent(this, LrcActivity::class.java)
        intent.putExtra("name", name)
        intent.putExtra("id", id)
        startActivityForResult(intent, searchIntentResult)
    }

    private fun refreshRecord() {
        val str = getRecord(pref!!)
        search_record.removeAllViews()
        if (str != null) {
            val arr = str.split(placeHolder)
            for (i in recordMax - 1 downTo 0) {
                if (arr.size > i) {
                    val btn = recordList[i] as Button
                    val arr2 = arr[i].split(placeHolder2)
                    btn.text = arr2[0]
                    btn.id = arr2[1].toInt()
                    search_record.addView(recordList[i])
                }
            }
        }
    }

    private fun refreshFavorite() {
        val str = getFavorite(pref!!)
        search_favorite.removeAllViews()
        if (str != null) {
            val arr = str.split(placeHolder)
            for (i in favoriteMax - 1 downTo 0) {
                if (arr.size > i) {
                    val btn = favoriteList[i] as Button
                    val arr2 = arr[i].split(placeHolder2)
                    if (arr2.size != 2) {
                        break; // when item is less than 2
                    }
                    btn.text = arr2[0]
                    btn.id = arr2[1].toInt()
                    search_favorite.addView(favoriteList[i])
                }
            }
        }
    }

}
