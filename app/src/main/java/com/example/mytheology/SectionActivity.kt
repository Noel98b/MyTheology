package com.example.mytheology

import android.content.Intent
import android.os.Bundle
import android.view.ActionMode
import android.view.View
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*

class SectionActivity : AppCompatActivity(), UpdateAndDeleteEntry {
    //Firebase
    lateinit var fireBaseService:FirebaseMapper
    var List:MutableList<Entry>?=null
    lateinit var adapter: EntryAdapter
    private var listViewItem : ListView?=null
    private var sectionID : String? = ""
    private var emptyMessage:TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_section)

        listViewItem = findViewById(R.id.entries_list)
        emptyMessage = findViewById(R.id.Emptymessage)

        fireBaseService = FirebaseMapper()

        //unpack bundle and create actionbar
        val b = intent.extras
        sectionID = b!!.getString("0")

        val actionBar = supportActionBar
            actionBar!!.title = b!!.getString("1")
            actionBar.setDisplayHomeAsUpEnabled(true)


        val s_fab = findViewById<View>(R.id.s_fab) as FloatingActionButton

        List = mutableListOf<Entry>()
        adapter = EntryAdapter(this, List!!)
        listViewItem!!.adapter = adapter
        fireBaseService.sectionReference.child(sectionID.toString()).child("entries").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                List!!.clear()
                addItemToList(snapshot)
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Es gab ein Problem", Toast.LENGTH_LONG).show()
            }
        })

        s_fab.setOnClickListener{ view ->

            /*
            intent.putExtras(b)
            val intent = Intent(this@SectionActivity, CreateEntryActivity::class.java)
            intent.putExtras(b)
            startActivity(intent)

             */
            val thisentry = Entry()
            val alertDialog = AlertDialog.Builder(this)
            val textEditText = EditText(this)
            alertDialog.setTitle("Erstelle einen neuen Eintrag")
            alertDialog.setView(textEditText)
            alertDialog.setPositiveButton("Create"){ dialog, i ->

                thisentry.title = textEditText.text.toString()
                thisentry.entry = ""
                var lastKey:String? = null
                var lastKeyInt = 0
                fireBaseService.sectionReference.child(sectionID.toString()).child("entries").push().setValue(thisentry)
            }
            alertDialog.show()
        }
    }

    private fun addItemToList(snapshot: DataSnapshot){

        val items = snapshot.children.iterator()
            while (items.hasNext()){
                val IndexedValue = items.next()
                val map = IndexedValue.getValue() as HashMap<String, Any>
                var entry = Entry()
                val Itemdata = MainModel.createList()
                entry.entryID = IndexedValue.key
                entry.title = map.get("title") as String?
                entry.entry = map.get("entry") as String?
                List!!.add(entry)
                if (!List!!.isEmpty()){
                    emptyMessage?.text  = ""
                }
        }
        adapter.notifyDataSetChanged()

    }

    override fun onItemDelete( entryID: String) {
        fireBaseService.onEntryDelete(entryID,sectionID.toString())
        adapter.notifyDataSetChanged()
    }

    override fun onSectionClick(entryID: String) {
        val b = Bundle()
        b.putString("0", entryID)
        b.putString("1", sectionID)
        val intent = Intent(this@SectionActivity, EditEntryActivity::class.java)
        intent.putExtras(b)
        startActivity(intent)
    }



}
