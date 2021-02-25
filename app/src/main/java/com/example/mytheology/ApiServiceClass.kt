package com.example.mytheology

import android.text.Html
import android.util.Log
import android.widget.Toast
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException

class ApiServiceClass {

    private var searchData: SearchPackage? = null
    private val client = OkHttpClient()
    private val credential = "c598fb63dc6793ca010d8bbe033cf15b"


    public fun search(term: String): SearchPackage? {
        val url = "https://api.scripture.api.bible/v1/bibles/95410db44ef800c1-01/search?query="
        val offset = "0"
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

    fun requestselectedBibleVerse(selectedBook:String, selectedChapter:String, spinner1SelectedPos:Int?, spinner2SelectedPos:Int?, selectedVerse:Any, selectedVerse2:Any):String
    {
        var result:String = "0"
        val url = "https://api.scripture.api.bible/v1/bibles/95410db44ef800c1-01/verses/"
        val selectedBook = selectedBook
        var selectedChapter = selectedChapter
        if (selectedChapter == null) {
            selectedChapter = "1"
        }
        var content: String? = ""

        if (spinner1SelectedPos!! == spinner2SelectedPos!!) {

           val request = Request.Builder()
                    .url("$url$selectedBook.$selectedChapter.$selectedVerse")
                    .addHeader("api-key", credential)
                    .build()
            Log.d("URL:", "$url$selectedBook.$selectedChapter.$selectedVerse")

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    result = "2"
                }

                override fun onResponse(call: Call, response: Response) {
                    val body = response?.body?.string()
                    val gson = GsonBuilder().create()
                    val data = gson.fromJson(body, Package::class.java)
                    content = data.data.content

                    Log.d("URL:", data.data.content)
                    //01 !Wait for response instead of sleep
                }

            })
            Thread.sleep(900)
           result = Html.fromHtml(content).toString()
        } else if (spinner1SelectedPos!! > spinner2SelectedPos!!) {
            result = "1" //kein gültiger Versbereich
        } else {

            val request = Request.Builder()
                    .url("$url$selectedBook.$selectedChapter.$selectedVerse-$selectedBook.$selectedChapter.$selectedVerse2")
                    .addHeader("api-key", credential)
                    .build()
            Log.d("URL:", "$url$selectedBook.$selectedChapter.$selectedVerse2-$selectedChapter.$selectedVerse2")

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    result = "2"
                }

                override fun onResponse(call: Call, response: Response) {
                    val body = response?.body?.string()
                    val gson = GsonBuilder().create()
                    val data = gson.fromJson(body, Package::class.java)
                    content = data.data.content

                    Log.d("URL:", data.data.content)
                    //01 !Wait for response instead of sleep
                }
            })
            Thread.sleep(900)
            result = Html.fromHtml(content).toString()
        }

        return result
    }





    class Package(val data: Data)

    class VersePackage(val data:VerseData)

    class SearchPackage(val data: SearchData)

    class Data(val id: String, val bibleId: String, val number: String, val bookId: String, val reference: String, val content: String)

    class VerseData(val verseCount: String)

    class SearchData(val query:String, val total:String, val verseCount: String,  val verses:ArrayList<Result> )

    class Result(val reference: String, val text:String)
}