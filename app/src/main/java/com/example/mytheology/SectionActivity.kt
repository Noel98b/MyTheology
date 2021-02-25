package com.example.mytheology

import android.content.Intent
import android.os.Bundle
import android.view.ActionMode
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import com.itextpdf.text.BaseColor
import com.itextpdf.text.Document
import com.itextpdf.text.PageSize
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfWriter
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.util.jar.Manifest

class SectionActivity : AppCompatActivity(), UpdateAndDeleteEntry {
    //Firebase
    lateinit var fireBaseService:FirebaseMapper
    var List:MutableList<Entry>?=null
    lateinit var adapter: EntryAdapter
    private lateinit var listViewItem : ListView
    private lateinit var sectionID : String
    private lateinit var emptyMessage:TextView
    private lateinit var createPdfButton: Button
    private lateinit var filename:String
    private lateinit var pdfService: PdfServiceClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_section)

        listViewItem = findViewById(R.id.entries_list)
        emptyMessage = findViewById(R.id.Emptymessage)

        fireBaseService = FirebaseMapper()
        pdfService = PdfServiceClass()

        //unpack bundle and create actionbar
        val b = intent.extras
        sectionID = b!!.getString("0").toString()

        val actionBar = supportActionBar
            actionBar!!.title = b!!.getString("1")
            actionBar.setDisplayHomeAsUpEnabled(true)

        createPdfButton = findViewById<Button>(R.id.createPDF)
        filename = sectionID

        Dexter.withActivity(this).withPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object:PermissionListener{
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    createPdfButton.setOnClickListener(){
                        pdfService.createPDFFile(Common.getAppPath(this@SectionActivity)+filename)
                    }
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {

                }

            }).check()

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
