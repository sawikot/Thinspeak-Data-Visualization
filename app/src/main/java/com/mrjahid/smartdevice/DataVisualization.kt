package com.mrjahid.smartdevice

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.*
import org.json.JSONObject

class DataVisualization : AppCompatActivity() {

    lateinit var lineChart: LineChart
    lateinit var lineData: LineData
    lateinit var lineDataSet: LineDataSet
    lateinit var lineEntriesList: ArrayList<Entry>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_visualization)
        lineChart = findViewById(R.id.idBarChart)
        jsonFeed(getData(this,"responseData","").toString(),"field3")


        lineDataSet = LineDataSet(lineEntriesList, "Bar Chart Data")
        lineData = LineData(lineDataSet)
        lineChart.data = lineData
        lineDataSet.valueTextColor = Color.BLACK
        lineDataSet.setColor(ContextCompat.getColor(this,R.color.purple_200))
        lineDataSet.valueTextSize = 16f
        lineChart.description.isEnabled = false


    }


    fun jsonFeed(responseData:String,fieldName:String){
        try {

            val ob = JSONObject(responseData)
            val arr = ob.getJSONArray("feeds")
            lineEntriesList = ArrayList()
            for (jsonIndex in 0 until arr.length()) {

                val field = arr.getJSONObject(jsonIndex).get(fieldName).toString()
                lineEntriesList.add(BarEntry(jsonIndex.toFloat(),field.toFloat()))
                Log.e("JJJ", field)
            }

        } catch (e: Exception) {
            Log.e("JJJ", "error")
        }
    }

    fun getData(con: Context?, variable: String?, defaultValue: String?): String? {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(con)
        return prefs.getString(variable, defaultValue)
    }
}