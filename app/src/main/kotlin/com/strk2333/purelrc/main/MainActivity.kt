package com.strk2333.purelrc.main

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.strk2333.purelrc.R
import com.strk2333.purelrc.Application
import com.strk2333.purelrc.view.SearchActivity
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), IMainView {

    @Inject
    lateinit var presenter : MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DaggerMainComponent.builder().applicationComponent(Application.component).mainModule(MainModule(this)).build().inject(this)

        mainSearch.setOnClickListener {
            val i = Intent(this, SearchActivity::class.java)
            startActivity(i)
        }
    }
}
