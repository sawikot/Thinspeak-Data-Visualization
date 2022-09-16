package com.mrjahid.smartdevice

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.IMarker
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class DataVisualization : AppCompatActivity() {

    lateinit var lineChart: LineChart
    lateinit var lineData: LineData
    lateinit var lineDataSet: LineDataSet
    lateinit var lineEntriesList: ArrayList<Entry>
    lateinit var warning_lable: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_visualization)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = getData(this,"field_name","").toString()
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        warning_lable = findViewById(R.id.warning_text)


        lineChart = findViewById(R.id.idBarChart)
        jsonFeed(getData(this,"responseData","").toString(),getData(this,"field_type","").toString())
        val marker: IMarker = CustomMarkerView(this, R.layout.custom_marker_view)
        lineChart.marker=marker
        lineChart.xAxis.valueFormatter = LineChartXAxisValueFormatter()
        lineChart.setTouchEnabled(true)


        lineDataSet = LineDataSet(lineEntriesList, getData(this,"field_name","").toString()+" Chart Data")
        lineData = LineData(lineDataSet)
        lineChart.data = lineData

        lineDataSet.valueTextColor = Color.BLACK
        lineDataSet.color = ContextCompat.getColor(this,R.color.purple_200)
        lineDataSet.valueTextSize = 18f
        lineDataSet.setDrawValues(false)
        lineDataSet.setDrawCircles(true)
        lineDataSet.setCircleColor(Color.BLACK)
        lineDataSet.setDrawCircleHole(false)
        lineDataSet.lineWidth=3f
        lineChart.description.isEnabled = false


    }


    private fun jsonFeed(responseData:String, fieldName:String){
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


                if(field == "null"){
                    lineEntriesList.add(Entry(date.time.toFloat(),0f))
                }else{
                    lineEntriesList.add(Entry(date.time.toFloat(),field.toFloat()))
                }
            }

            val field_name:String=getData(this,"field_name","").toString()
            if(field_name == "Temp"){
                if(lineEntriesList[lineEntriesList.size-1].y<20){
                    warning_lable.visibility= View.VISIBLE
                    warning_lable.text="Ortam sıcaklığı düşük."
                }
            }else if(field_name == "CO2"){
                if(lineEntriesList[lineEntriesList.size-1].y<1000){
                    warning_lable.visibility= View.VISIBLE
                    warning_lable.text="Karbondiyoksit seviyesi yüksek."
                }
            }else if(field_name == "WaterLevel"){
                if(lineEntriesList[lineEntriesList.size-1].y<2){
                    warning_lable.visibility= View.VISIBLE
                    warning_lable.text="Sistemdeki su tükenmek üzere. Lütfen su ekleyin."
                }
            }

        } catch (e: Exception) {
            Log.e("JsonExc", e.message.toString())
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    fun getData(con: Context?, variable: String?, defaultValue: String?): String? {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(con)
        return prefs.getString(variable, defaultValue)
    }
}