package com.mrjahid.smartdevice

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private val itemsList = ArrayList<Word>()
    private lateinit var customAdapter: CustomAdapter
    var recyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val word: Word = Word("", "")
        word.title = "Temp"
        word.img = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/79/A_%28capital_and_small%29.svg/800px-A_%28capital_and_small%29.svg.png"
        itemsList.add(word)

        recyclerView = findViewById(R.id.recyclerView)
        customAdapter = CustomAdapter(itemsList)

        val layoutManager = GridLayoutManager(applicationContext, 2)
        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.adapter = customAdapter


        val intent = Intent(this, DataVisualization::class.java)
        customAdapter.setOnItemClickListener(object : CustomAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                startActivity(intent)
            }


        })
    }
}