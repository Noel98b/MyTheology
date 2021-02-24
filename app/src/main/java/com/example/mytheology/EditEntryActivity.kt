package com.example.mytheology

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException
import com.example.mytheology.ApiServiceClass


class EditEntryActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    lateinit var fireBaseService:FirebaseMapper
    lateinit var adapter: EntryAdapter
    lateinit var listViewItem: ListView
    lateinit var entryId: String
    lateinit var verses: String
    private var versesArray = arrayOf<String?>()
    private var chapters = arrayOf<String?>()
    lateinit var apiService:ApiServiceClass

    //Data which can be initialized and stored locally to inhibit data exchange with api
    var booksAndChaptersPairList = arrayListOf(Pair("1.Mose", 50), Pair("2.Mose", 40), Pair("3.Mose", 27), Pair("4.Mose", 36), Pair("5.Mose", 34), Pair("Josua", 24), Pair("Richter", 21), Pair("Ruth", 4),
            Pair("1.Samuel", 31), Pair("2.Samuel", 24), Pair("1.Könige", 22), Pair("2.Könige", 25), Pair("1.Chronik", 29), Pair("2.Chronik", 36), Pair("Esra", 10), Pair("Nehemia", 13), Pair("Esther", 10), Pair("Hiob", 42),
            Pair("Psalmen", 150), Pair("Sprüche", 31), Pair("Prediger", 12), Pair("Hohelied", 8), Pair("Jesaja", 66), Pair("Jeremia", 52), Pair("Klagelieder", 5), Pair("Hesekiel", 48), Pair("Daniel", 12), Pair("Hosea", 14),
            Pair("Joel", 3), Pair("Amos", 9), Pair("Obadja", 1), Pair("Jona", 4), Pair("Micha", 7), Pair("Nahum", 3), Pair("Habakuk", 3), Pair("Zephanja", 3), Pair("Haggai", 2),
            Pair("Sacharja", 14), Pair("Maleachi", 4), Pair("Matthäus", 28), Pair("Markus", 16), Pair("Lukas", 24), Pair("Johannes", 21), Pair("Apostelgeschichte", 28), Pair("Römer", 16), Pair("1.Korinther", 16), Pair("2.Korinther", 13),
            Pair("Galater", 6), Pair("Epheser", 6), Pair("Philipper", 4), Pair("Kolosser", 4), Pair("1.Tessalonicher", 5), Pair("2.Tessalonicher", 3), Pair("1.Timotheus", 6), Pair("2.Timotheus", 4), Pair("Titus", 3), Pair("Philemon", 1),
            Pair("Hebäer", 13), Pair("Jakobus", 5), Pair("1.Petrus", 5), Pair("2.Petrus", 3), Pair("1.Johannes", 5), Pair("2.Johannes", 1), Pair("3.Johannes", 1), Pair("Judas", 1), Pair("Offenbarung", 22))

    var booksAndAbbrevationList = arrayListOf(Pair("1.Mose", "GEN"), Pair("2.Mose", "EXO"), Pair("3.Mose", "LEV"), Pair("4.Mose", "NUM"), Pair("5.Mose", "DEU"), Pair("Josua", "JOS"), Pair("Richter", "JDG"), Pair("Ruth", "RUT"),
            Pair("1.Samuel", "1SA"), Pair("2.Samuel", "2SA"), Pair("1.Könige", "1KI"), Pair("2.Könige", "2KI"), Pair("1.Chronik", "1CH"), Pair("2.Chronik", "2CH"), Pair("Esra", "EZR"), Pair("Nehemia", "NEH"), Pair("Esther", "EST"), Pair("Hiob", "JOB"),
            Pair("Psalmen", "PSA"), Pair("Sprüche", "PRO"), Pair("Prediger", "ECC"), Pair("Hohelied", "SNG"), Pair("Jesaja", "ISA"), Pair("Jeremia", "JER"), Pair("Klagelieder", "LAM"), Pair("Hesekiel", "EZK"), Pair("Daniel", "DAN"), Pair("Hosea", "HOS"),
            Pair("Joel", "JOL"), Pair("Amos", "AMO"), Pair("Obadja", "OBA"), Pair("Jona", "JON"), Pair("Micha", "MIC"), Pair("Nahum", "NAM"), Pair("Habakuk", "HAB"), Pair("Zephanja", "ZEP"), Pair("Haggai", "HAG"),
            Pair("Sacharja", "ZEC"), Pair("Maleachi", "MAL"), Pair("Matthäus", "MAT"), Pair("Markus", "MRK"), Pair("Lukas", "LUK"), Pair("Johannes", "JHN"), Pair("Apostelgeschichte", "ACT"), Pair("Römer", "ROM"), Pair("1.Korinther", "1CO"), Pair("2.Korinther", "2CO"),
            Pair("Galater", "GAL"), Pair("Epheser", "EPH"), Pair("Philipper", "PHP"), Pair("Kolosser", "COL"), Pair("1.Tessalonicher", "1TH"), Pair("2.Tessalonicher", "2TH"), Pair("1.Timotheus", "1TI"), Pair("2.Timotheus", "2TI"), Pair("Titus", "TIT"), Pair("Philemon", "PHM"),
            Pair("Hebäer", "HEB"), Pair("Jakobus", "JAS"), Pair("1.Petrus", "1PE"), Pair("2.Petrus", "2PE"), Pair("1.Johannes", "1JN"), Pair("2.Johannes", "2JN"), Pair("3.Johannes", "3JN"), Pair("Judas", "JUD"), Pair("Offenbarung", "REV"))

    //declare all spinners
    private lateinit var bookSpinner: Spinner
    private lateinit var chapterSpinner: Spinner
    private lateinit var verseSpinner: Spinner
    private lateinit var verseSpinner2: Spinner
    private val allBooks = arrayOfNulls<String?>(66) //The Bible consists of 66 Books
    private lateinit var title: TextView
    private lateinit var text: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_entry)

        fireBaseService = FirebaseMapper()
        apiService = ApiServiceClass()

        //declare all spinners
        bookSpinner = findViewById<Spinner>(R.id.Book)
        chapterSpinner = findViewById<Spinner>(R.id.Chapter)
        verseSpinner = findViewById<Spinner>(R.id.Verse)
        verseSpinner2 = findViewById<Spinner>(R.id.Verse2)

         title = findViewById<TextView>(R.id.EditTitle)
         text= findViewById<TextView>(R.id.EditText)

        //unpack bundle and create actionbar
        val b = intent.extras
        entryId = b!!.getString("0").toString()
        val actionBar = supportActionBar
        actionBar!!.title = "Eintrag bearbeiten"
        actionBar.setDisplayHomeAsUpEnabled(true)

        val sectionID = b!!.getString("1")
        fireBaseService.sectionReference.child(sectionID.toString()).child("entries").child(entryId).addValueEventListener(object :
                ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value != null) {
                    val map = snapshot.value as HashMap<String, String>
                    title?.text = map["title"] as String?
                    text?.text = map["entry"] as String?
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Es gab ein Problem", Toast.LENGTH_LONG).show()
            }
        })

        val save: Button = findViewById<Button>(R.id.Save)
        save.setOnClickListener() {

            if (sectionID != null) {
                fireBaseService.saveEntry(entryId,sectionID, text?.text.toString(), title?.text.toString())
            }
            Toast.makeText(applicationContext, "Änderungen gespeichert.", Toast.LENGTH_LONG).show()
        }

        //Bookspinner can be filled as its values are static
        for (i in 0..booksAndChaptersPairList.size - 1) {
            allBooks[i] = booksAndChaptersPairList[i].first
        }
        var bookAdapter = ArrayAdapter(this, R.layout.spinner_item, allBooks)
        bookSpinner!!.adapter = bookAdapter
        bookSpinner!!.onItemSelectedListener = this



        chapterSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                getVerses(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        verseSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                verseSpinner2?.setSelection(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        getVerses(null)

        val searchbutton :Button = findViewById(R.id.searchButton) as Button
        searchbutton.setOnClickListener(){
            val bForSearch = Bundle()
            bForSearch.putString("0", entryId)
            bForSearch.putString("1", sectionID)
            val intent2 = Intent(this@EditEntryActivity, SearchActivity::class.java)
            intent2.putExtras(bForSearch)
            startActivity(intent2)
        }

        val add: Button = findViewById(R.id.add) as Button
        add.setOnClickListener() {
            val client = OkHttpClient()
            val credential = "c598fb63dc6793ca010d8bbe033cf15b"
            val url = "https://api.scripture.api.bible/v1/bibles/95410db44ef800c1-01/verses/"
            val selectedBook = booksAndAbbrevationList[bookSpinner!!.selectedItemPosition].second
            var selectedChapter = chapterSpinner!!.selectedItem
            if (selectedChapter == null){
                selectedChapter = "1"
            }
            var content: String? = ""

            if( versesArray[verseSpinner!!.selectedItemPosition]?.toInt()!! == versesArray[verseSpinner2!!.selectedItemPosition]?.toInt()!!) {

                val selectedVerse = verseSpinner!!.selectedItem

                val request = Request.Builder()
                        .url("$url$selectedBook.$selectedChapter.$selectedVerse")
                        .addHeader("api-key", credential)
                        .build()
                Log.d("URL:", "$url$selectedBook.$selectedChapter.$selectedVerse")

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Toast.makeText(applicationContext, "No response", Toast.LENGTH_LONG).show()
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
                text?.text = text?.text.toString() + Html.fromHtml(content)
            }else if ( versesArray[verseSpinner!!.selectedItemPosition]?.toInt()!! > versesArray[verseSpinner2!!.selectedItemPosition]?.toInt()!!){
                Toast.makeText(applicationContext, "Please enter a valid verse range ", Toast.LENGTH_LONG).show()
            }else{
                val selectedVerse1 = verseSpinner!!.selectedItem
                val selectedVerse2 = verseSpinner2!!.selectedItem

                val request = Request.Builder()
                        .url("$url$selectedBook.$selectedChapter.$selectedVerse1-$selectedBook.$selectedChapter.$selectedVerse2")
                        .addHeader("api-key", credential)
                        .build()
                Log.d("URL:", "$url$selectedBook.$selectedChapter.$selectedVerse1-$selectedChapter.$selectedVerse2")

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Toast.makeText(applicationContext, "No response", Toast.LENGTH_LONG).show()
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
                text?.text = Html.fromHtml(content)
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var ct = 1
        chapters = arrayOfNulls<String?>(booksAndChaptersPairList[position].second)  //There is a maximum of 150 Psalms (which is the book with the most chapters)
        for (i in 0 until booksAndChaptersPairList[position].second) {
            chapters[i] = ct.toString()
            ct += 1
        }
        var chapterAdapter = ArrayAdapter(this, R.layout.spinner_item, chapters)
        chapterSpinner?.adapter = chapterAdapter
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    private fun updatespinner(arr: Array<String?>) {
        val verseAdapter: ArrayAdapter<String> = ArrayAdapter<String>(applicationContext, R.layout.spinner_item, arr)
        this.verseSpinner?.adapter = verseAdapter
        val verseAdapter2: ArrayAdapter<String> = ArrayAdapter<String>(applicationContext, R.layout.spinner_item, arr)
        this.verseSpinner2?.adapter = verseAdapter2
    }

    fun getVerses(position: Int?){
        val client = OkHttpClient()
        val credential = "c598fb63dc6793ca010d8bbe033cf15b"
        val url = "https://api.scripture.api.bible/v1/bibles/95410db44ef800c1-01/chapters/"
        val selectedBook = booksAndAbbrevationList[bookSpinner!!.selectedItemPosition].second
        var selectedChapter = position?.plus(1).toString()
        if (chapterSpinner?.selectedItem == null){
             selectedChapter = "1"
        }
        val request = Request.Builder()
                .url("$url$selectedBook.$selectedChapter")
                .addHeader("api-key", credential)
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Toast.makeText(applicationContext, "No response", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call, response: Response) {

                Log.d("response", "response");
                val body = response?.body?.string()
                val gson = GsonBuilder().create()
                val verseData = gson.fromJson(body, VersePackage::class.java)
                val content = verseData.data.verseCount
                versesArray = arrayOfNulls<String?>(content.toInt())
                var ct = 1
                for (i in 0 until content.toInt()) {
                    versesArray[i] = ct.toString()
                    ct = ct + 1
                }
            }
        })
        Thread.sleep(900) //LOADINGANIMATION
        updatespinner(versesArray)
    }

class Package(val data: Data)

class VersePackage(val data:VerseData)

class SearchPackage(val data: SearchData)

class Data(val id: String, val bibleId: String, val number: String, val bookId: String, val reference: String, val content: String)

class VerseData(val verseCount: String)

class SearchData(val query:String, val total:String, val verseCount: String,  val verses:ArrayList<Result> )

class Result(val reference: String, val text:String)
}



