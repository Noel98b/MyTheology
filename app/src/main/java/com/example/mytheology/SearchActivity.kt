package com.example.mytheology

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException

class SearchActivity : AppCompatActivity() {

    private var apiService:ApiServiceClass = ApiServiceClass()
    private var searchData: ApiServiceClass.SearchPackage? = null
    var searchTerm: TextView? = null
    var resultBox: LinearLayout? = null
    val lparams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
    )
    var offset:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)


        searchTerm = findViewById<TextView>(R.id.searchTerm)
        resultBox = findViewById<LinearLayout>(R.id.resultBox)

        val actionBar = supportActionBar
        actionBar!!.title = "Bibel Suche"
        actionBar.setDisplayHomeAsUpEnabled(false)

        val searchButton: ImageButton = findViewById<ImageButton>(R.id.searchButton)
        searchButton.setOnClickListener() {
            offset = 0
            searchTheAPI()
        }

        val nextResult: Button = findViewById(R.id.nextResults) as Button
        nextResult.setOnClickListener() {
            offset += 1
            searchTheAPI()
        }
    }

    fun TextView.setTextColor(color: Long) = this.setTextColor(color.toInt())

    fun searchTheAPI(){

        if(offset==0){
            resultBox?.removeAllViews()
        }
        val client = OkHttpClient()
        val credential = "c598fb63dc6793ca010d8bbe033cf15b"
        val url = "https://api.scripture.api.bible/v1/bibles/95410db44ef800c1-01/search?query="
        val term = searchTerm?.text
        val request = Request.Builder()
            .url("$url$term&offset=$offset")
            .addHeader("api-key", credential)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {

                Log.d("response", "response");
                val body = response?.body?.string()
                val gson = GsonBuilder().create()
                searchData = gson.fromJson(body, ApiServiceClass.SearchPackage::class.java)

            }
        })
        Thread.sleep(900) //LOADINGANIMATION

        if(offset==0) {
            Toast.makeText(
                applicationContext,
                searchData?.data?.total + " Ergebnisse insgesamt",
                Toast.LENGTH_LONG
            ).show()
        }else{
            Toast.makeText(
                applicationContext,
                "10 weitere Ergebnisse geladen.",
                Toast.LENGTH_LONG
            ).show()
        }

        for (item in searchData!!.data.verses){
            val tv = TextView(this)
            tv.setTextColor(0xff000000)
            tv.layoutParams = lparams
            tv.text = item.reference + ": " + item.text + "\n"
            tv.setTextIsSelectable(true)
            resultBox?.addView(tv)
        }
    }

}