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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DataVisualization : AppCompatActivity() {

    lateinit var lineChart: LineChart
    lateinit var lineData: LineData
    lateinit var lineDataSet: LineDataSet
    lateinit var lineEntriesList: ArrayList<Entry>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_visualization)
        var actionBar = getSupportActionBar()
        if (actionBar != null) {
            actionBar.title = getData(this,"field_name","").toString();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        lineChart = findViewById(R.id.idBarChart)
        jsonFeed(getData(this,"responseData","").toString(),getData(this,"field_type","").toString())

        lineChart.xAxis.valueFormatter = LineChartXAxisValueFormatter()
        lineChart.setTouchEnabled(true);


        lineDataSet = LineDataSet(lineEntriesList, getData(this,"field_name","").toString()+" Chart Data")
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

            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
//            val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm")

            for (jsonIndex in 0 until arr.length()) {

                val field = arr.getJSONObject(jsonIndex).get(fieldName).toString()
                val created_at = arr.getJSONObject(jsonIndex).get("created_at").toString()

                val date: Date = sdf.parse(created_at)


                lineEntriesList.add(Entry(date.time.toFloat(),field.toFloat()))
                Log.e("JJJ", date.time.toString())
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