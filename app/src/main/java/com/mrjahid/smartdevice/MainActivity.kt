package com.mrjahid.smartdevice

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.*
import okio.IOException
import org.json.JSONObject
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private val itemsList = ArrayList<Word>()
    private lateinit var customAdapter: CustomAdapter
    var recyclerView: RecyclerView? = null

    val img: IntArray = intArrayOf(
        R.drawable.temp,
        R.drawable.pressure,
        R.drawable.alti,
        R.drawable.humidity,
        R.drawable.co2,
        R.drawable.tvoc,
        R.drawable.waterlevel
    )
    val name = arrayOf("Temp", "Pressure", "Alti", "Humidity", "CO2", "TVOC", "WaterLevel")
    private val client = OkHttpClient()
    lateinit var dialog: AlertDialog

    private val field_value = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val user = getData(this, "checkLogin", "")
        if (user == "login") {
            val builder = AlertDialog.Builder(this)
            builder.setCancelable(false) // if you want user to wait for some process to finish,

            builder.setView(R.layout.layout_loading_dialog)
            dialog = builder.create()

            getShopData()


            recyclerView = findViewById(R.id.recyclerView)
            customAdapter = CustomAdapter(itemsList)

            val layoutManager = GridLayoutManager(applicationContext, 2)
            recyclerView!!.layoutManager = layoutManager
            recyclerView!!.adapter = customAdapter


            val intent = Intent(this, DataVisualization::class.java)
            customAdapter.setOnItemClickListener(object : CustomAdapter.onItemClickListener {
                override fun onItemClick(position: Int) {
                    var i: Int = position + 1
                    saveData(baseContext, "field_type", "field" + i)
                    saveData(baseContext, "field_name", name[position])
                    startActivity(intent)
                }


            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                saveData(baseContext, "checkLogin", "logout")
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                return true
            }

            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun saveData(con: Context, variable: String?, data: String?) {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(con)
        prefs.edit().putString(variable, data).commit()
    }

    fun getData(con: Context?, variable: String?, defaultValue: String?): String {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(con)
        return prefs.getString(variable, defaultValue).toString()
    }

    override fun onStart() {
        super.onStart()
        val user = getData(this, "checkLogin", "")
        if (user != "login") {

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }


    fun getShopData() {
        dialog.show()

        val request = Request.Builder()
            .url("https://api.thingspeak.com/channels/1706364/feeds.json?api_key=P5ZGP85WGXENCYSJ&results=100")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                dialog.dismiss();
                internet_Check_dialog()
                e.printStackTrace()
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call, response: Response) {

                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    try {

                        var responseData: String = response.body!!.string()
                        saveData(baseContext, "responseData", responseData)

                        val ob = JSONObject(responseData)
                        val arr = ob.getJSONArray("feeds")

                        var last_index = arr.length() - 1


                        field_value.add(arr.getJSONObject(last_index).get("field1").toString())
                        field_value.add(arr.getJSONObject(last_index).get("field2").toString())
                        field_value.add(arr.getJSONObject(last_index).get("field3").toString())
                        field_value.add(arr.getJSONObject(last_index).get("field4").toString())
                        field_value.add(arr.getJSONObject(last_index).get("field5").toString())
                        field_value.add(arr.getJSONObject(last_index).get("field6").toString())
                        field_value.add(arr.getJSONObject(last_index).get("field7").toString())


                        for ((i, item) in img.withIndex()) {
                            val word: Word = Word("", 0)
                            if(field_value[i] == "null"){
                                word.title = name[i] +": 0"
                            }else{
                                word.title = name[i] +": "+ field_value[i]
                            }

                            word.img = item
                            itemsList.add(word)
                        }

                        recyclerView!!.smoothScrollToPosition(0)
                        customAdapter.notifyDataSetChanged()

                        dialog.dismiss();

                    } catch (e: Exception) {
                        dialog.dismiss();

                    }

                }
            }

        })

    }


    fun internet_Check_dialog() {
        runOnUiThread {
            if (!isFinishing) {
                val builder = AlertDialog.Builder(this)

                builder.setTitle("Error")

                builder.setMessage("Check your internet")
                builder.setIcon(android.R.drawable.ic_dialog_alert)

                builder.setPositiveButton("Retry") { dialogInterface, which ->

                    try {

                        getShopData()

                    } catch (e: Exception) {

                    }

                }

                builder.setNeutralButton("Cancel") { dialogInterface, which ->
                    finish()
                }

                val alertDialog: AlertDialog = builder.create()
                alertDialog.setCancelable(false)
                alertDialog.show()
            }
        }
    }
}