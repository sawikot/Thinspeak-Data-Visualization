package com.mrjahid.smartdevice

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import okhttp3.*
import okio.IOException
import androidx.preference.PreferenceManager

class LoginActivity : AppCompatActivity() {
    var btnLogin: Button? = null
    private val client = OkHttpClient()
    lateinit var dialog: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        btnLogin  = findViewById(R.id.btnLogin)

        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false) // if you want user to wait for some process to finish,

        builder.setView(R.layout.layout_loading_dialog)
        dialog = builder.create()


        btnLogin!!.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
        getShopData()
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

                        var responseData: String =response.body!!.string()
                        saveData(baseContext, "responseData", responseData)

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

                builder.setTitle("Error" )

                builder.setMessage("Check your internet")
                builder.setIcon(android.R.drawable.ic_dialog_alert)

                builder.setPositiveButton("Retry"){dialogInterface, which ->

                    try {

                        getShopData()

                    } catch (e: Exception) {

                    }

                }

                builder.setNeutralButton("Cancel"){dialogInterface , which ->
                    finish()
                }

                val alertDialog: AlertDialog = builder.create()
                alertDialog.setCancelable(false)
                alertDialog.show()
            }
        }
    }

    fun saveData(con: Context, variable: String?, data: String?) {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(con)
        prefs.edit().putString(variable, data).commit()
    }

    fun getData(con: Context?, variable: String?, defaultValue: String?): String? {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(con)
        return prefs.getString(variable, defaultValue)
    }
}