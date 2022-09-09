package com.mrjahid.smartdevice

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import okhttp3.*
import okio.IOException
import androidx.preference.PreferenceManager
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {
    var btnLogin: Button? = null

    var etLoginEmail: TextInputEditText? = null
    var etLoginPassword: TextInputEditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        saveData(baseContext, "userName", "faux")
        saveData(baseContext, "passWord", "admin")

        etLoginEmail  = findViewById(R.id.etLoginEmail)
        etLoginPassword  = findViewById(R.id.etLoginPass)

        btnLogin = findViewById(R.id.btnLogin)



        btnLogin!!.setOnClickListener {
            val userName:String = getData(this, "userName", "");
            val passWord:String = getData(this, "passWord", "");

            val email = etLoginEmail!!.text.toString()
            val password = etLoginPassword!!.text.toString()

            if ((userName == email) and (passWord == password)) {
                saveData(baseContext, "checkLogin", "login")
                val intent = Intent(this, MainActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }else{
                Toast.makeText(this,"Wrong UserName Or Password",Toast.LENGTH_SHORT).show()
            }
        }

    }



    fun saveData(con: Context, variable: String?, data: String?) {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(con)
        prefs.edit().putString(variable, data).commit()
    }

    fun getData(con: Context?, variable: String?, defaultValue: String?): String {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(con)
        return prefs.getString(variable, defaultValue).toString()
    }
}