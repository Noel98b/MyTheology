package com.example.mytheology

import android.util.Log
import android.widget.Toast
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException

class ApiServiceClass {

    private var searchData: SearchPackage? = null


    public fun search(term:String): SearchPackage? {
        val client = OkHttpClient()
        val credential = "c598fb63dc6793ca010d8bbe033cf15b"
        val url = "https://api.scripture.api.bible/v1/bibles/95410db44ef800c1-01/search?query="
        val offset ="0"
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
                searchData = gson.fromJson(body, SearchPackage::class.java)

            }
        })
        Thread.sleep(900) //LOADINGANIMATION
        return searchData
    }





    class Package(val data: Data)

    class VersePackage(val data:VerseData)

    class SearchPackage(val data: SearchData)

    class Data(val id: String, val bibleId: String, val number: String, val bookId: String, val reference: String, val content: String)

    class VerseData(val verseCount: String)

    class SearchData(val query:String, val total:String, val verseCount: String,  val verses:ArrayList<Result> )

    class Result(val reference: String, val text:String)
}