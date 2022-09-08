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

    val img: IntArray = intArrayOf(R.drawable.temp, R.drawable.pressure, R.drawable.alti, R.drawable.humidity, R.drawable.co2,R.drawable.tvoc,R.drawable.waterlevel)
    val name = arrayOf("Temp", "Pressure", "Alti", "Humidity", "CO2", "TVOC", "WaterLevel")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        for ((i, item) in img.withIndex()) {
            val word: Word = Word("", 0)
            word.title = name[i]
            word.img = item
            itemsList.add(word)
        }

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