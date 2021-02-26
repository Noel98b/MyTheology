package com.example.mytheology

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.karumi.dexter.Dexter
import java.util.jar.Manifest


class MainActivity : AppCompatActivity(), UpdateAndDelete {

    lateinit var List: MutableList<MainModel>
    lateinit var adapter: AdapterClass
    lateinit var listViewItem : ListView
    lateinit var MainEmptyMessage: TextView
    lateinit var fireBaseService:FirebaseMapper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fireBaseService = FirebaseMapper()

        fireBaseService.currentUser =  intent.extras!!.getString("user_id")
        //Main Elements
        listViewItem = findViewById(R.id.topics_list)
        MainEmptyMessage = findViewById(R.id.MainEmptyMessage)

        val fab = findViewById<View>(R.id.fab) as FloatingActionButton

        fab.setOnClickListener{ view ->
            val alertDialog = AlertDialog.Builder(this)
            val textEditText = EditText(this)
            alertDialog.setTitle("Erstelle ein Thema")
            alertDialog.setView(textEditText)
            alertDialog.setPositiveButton("Hinzufügen"){ dialog, i ->
                val sectionListElement = MainModel.createList()
                sectionListElement.sectionTitle= textEditText.text.toString()
                sectionListElement.done = false
                sectionListElement.entries = arrayListOf<Entry>()
                fireBaseService.newSection(sectionListElement)
                Toast.makeText(
                        this,
                        "Neues Thema " + sectionListElement.sectionTitle + " wurde erstellt",
                        Toast.LENGTH_LONG
                ).show()
            }
            alertDialog.show()
        }

        List = mutableListOf<MainModel>()
        adapter = AdapterClass(this, List!!)
        listViewItem!!.adapter = adapter
        fireBaseService.database.child(fireBaseService.currentUser!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                List!!.clear()
                addItemToList(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Es gab ein Problem", Toast.LENGTH_LONG).show()
            }
        })
    }


    private fun addItemToList(snapshot: DataSnapshot){

        val items = snapshot.children.iterator()
        while (items.hasNext()){
            val indexedValue = items.next()
            val map = indexedValue.value as HashMap<String, Any>
            val name = map["sectionTitle"] as String?
            val uid = map["uid"] as String?
            var m1 = MainModel()
            m1.sectionTitle = name
            m1.UID = uid
            List!!.add(m1)
            if (!List!!.isEmpty()){
                MainEmptyMessage?.text  = ""
            }
        }
        adapter.notifyDataSetChanged()
    }

  override fun onItemDelete(itemUID: String) {
        fireBaseService.onItemDelete(itemUID)
        adapter.notifyDataSetChanged()
    }

    override fun onSectionClick(itemUID: String, title:String ) {
        val b = Bundle()
        b.putString("1", title)
        b.putString("0", itemUID)//Title
        b.putString("user_id", fireBaseService.currentUser)
        intent.putExtras(b)
        val intent = Intent(this@MainActivity, SectionActivity::class.java)
        intent.putExtras(b)
        startActivity(intent)
    }
}