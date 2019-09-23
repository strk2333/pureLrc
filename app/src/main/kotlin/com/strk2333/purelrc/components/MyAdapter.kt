package com.strk2333.purelrc.components
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.strk2333.purelrc.R

import java.util.ArrayList

class MyAdapter(datas: ArrayList<MutableMap<String, String>>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    var datas: ArrayList<MutableMap<String, String>>? = null
    private var clickCallBack: ItemClickCallBack? = null
    private var clickFavCallBack: ItemClickCallBack? = null

    fun setClickCallBack(clickCallBack: ItemClickCallBack) {
        this.clickCallBack = clickCallBack
    }

    fun setClickFavCallBack(clickFavCallBack: ItemClickCallBack) {
        this.clickFavCallBack = clickFavCallBack
    }

    interface ItemClickCallBack {
        fun onItemClick(pos: Int)
    }

    init {
        this.datas = datas
    }

    //创建新View，被LayoutManager所调用
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.search_res_item, viewGroup, false)
        return ViewHolder(view)
    }

    //将数据与界面进行绑定的操作
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.mBtn.text = datas!!.get(position).get("name")
        viewHolder.mFavBtn.text = datas!!.get(position).get("fav")
        viewHolder.mBtn.setOnClickListener {
            if (clickCallBack != null) {
                clickCallBack!!.onItemClick(position)
            }
        }
        viewHolder.mFavBtn.setOnClickListener {
            if (clickFavCallBack != null) {
                clickFavCallBack!!.onItemClick(position)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: List<Any>?) {
        super.onBindViewHolder(holder, position, payloads)
    }

    //获取数据的数量
    override fun getItemCount(): Int {
        return datas!!.size
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var mBtn: Button
        var mFavBtn: Button
        init {
            mBtn = view.findViewById(R.id.search_res_item_btn) as Button
            mFavBtn = view.findViewById(R.id.search_res_item_favorite) as Button
        }
    }
}
