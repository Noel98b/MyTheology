package com.example.mytheology

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException
import java.security.acl.Group

class SearchActivity : AppCompatActivity() {


    private var fireBaseService = FirebaseMapper()
    private var apiService:ApiServiceClass = ApiServiceClass()
    private var searchData: ApiServiceClass.SearchPackage? = null
    private var b:Bundle?=null
    var searchTerm: TextView? = null
    var resultBox: LinearLayout? = null
    val lparams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
    )
    var offset:Int = 0
    lateinit var entryID:String
    lateinit var sectionID:String
    lateinit var officialText:String
    lateinit var officialTitle:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        b = intent.extras
        entryID = b!!.getString("0").toString()
        sectionID = b!!.getString("1").toString()

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

        fireBaseService.sectionReference.child(sectionID.toString()).child("entries").child(entryID).addValueEventListener(object :
                ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.getValue() != null) {
                    val map = snapshot.getValue() as HashMap<String, String>
                    officialText = (map["entry"]).toString()
                    officialTitle  = (map["title"]).toString()
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
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
        if (searchData!=null){
            for (item in searchData!!.data.verses){
                val gr = RadioGroup(this)
                val addBtn = Button(this)
                val tv = TextView(this)
                gr.orientation = RadioGroup.HORIZONTAL
                addBtn.text = "+"
                addBtn.setBackgroundColor(-16711681)
                tv.setTextColor(0xff000000)
                tv.layoutParams = lparams
                tv.text = item.reference + ": " + item.text + "\n"
                tv.width = 1000
                tv.setTextIsSelectable(true)
                gr.addView(tv)
                gr.addView(addBtn)
                resultBox?.addView(gr)
                addBtn.setOnClickListener(){
                    val newtext = officialText + "\n" + tv.text
                    fireBaseService.saveEntry(entryID, sectionID, newtext, officialTitle)
                    Toast.makeText(
                            applicationContext,
                            "Vers wurde hinzugefügt.",
                            Toast.LENGTH_LONG
                    ).show()
                }
                /*
                val resultb = Bundle()
                resultb.putString("0", this.b!!.getString("0").toString())
                resultb.putString("1", this.b!!.getString("1").toString())
                resultb.putString("2", tv.text.toString())
                val resultIntent = Intent()
                resultIntent.putExtras(resultb)
                setResult(Activity.RESULT_OK, resultIntent)
                */
            }
        }
    }
}