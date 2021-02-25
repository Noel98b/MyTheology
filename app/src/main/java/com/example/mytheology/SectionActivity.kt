package com.example.mytheology

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.util.Log
import android.view.ActionMode
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
    private lateinit var dataforPDF:MainModel

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
        dataforPDF = MainModel()
        dataforPDF.sectionTitle = b!!.getString("1").toString()
        dataforPDF.entries = arrayListOf()
        initEntry()

        Dexter.withActivity(this).withPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object:PermissionListener{
                @RequiresApi(Build.VERSION_CODES.KITKAT)
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    createPdfButton.setOnClickListener(){

                        if( dataforPDF.entries!!.size>0) {

                            pdfService.createPDFFile(Common.getAppPath(this@SectionActivity) + filename, dataforPDF)
                            printPDF()
                        }else{
                            Toast.makeText(applicationContext, "Keine Einträge vorhanden", Toast.LENGTH_LONG).show()
                        }
                    }
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    createPdfButton.isEnabled = false
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

    private fun initEntry(){
        fireBaseService.sectionReference.child(sectionID.toString()).child("entries").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                List!!.clear()
                addItemToList(snapshot)
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Es gab ein Problem", Toast.LENGTH_LONG).show()
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun printPDF() {
            val printManager = getSystemService(Context.PRINT_SERVICE) as PrintManager
            try {
                val printAdapter = PdfDocumentAdapter(this@SectionActivity, Common.getAppPath(this@SectionActivity)+filename)
                printManager.print("Document", printAdapter, PrintAttributes.Builder().build())
            }catch (e: Exception){
                Log.e("Printerror", ""+e.message)
            }
    }


    private fun addItemToList(snapshot: DataSnapshot){

        val items = snapshot.children.iterator()
            while (items.hasNext()){
                val IndexedValue = items.next()
                val map = IndexedValue.getValue() as HashMap<String, Any>
                var entry = Entry()
                entry.entryID = IndexedValue.key
                entry.title = map.get("title") as String?
                entry.entry = map.get("entry") as String?
                dataforPDF.entries?.add(entry)
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
        //delete from object
        val size1 = dataforPDF.entries?.size
        val index:Int? = findIndex(dataforPDF.entries, entryID)
        if (index != null) {
            dataforPDF.entries?.removeAt(index)
        }
        val size2 = dataforPDF.entries?.size

        Toast.makeText(applicationContext, size1.toString()+" " + size2.toString(), Toast.LENGTH_LONG).show()
    }

    fun findIndex(arr: ArrayList<Entry>?, item: String): Int? {
            return (arr!!.indices)
                    .firstOrNull { i: Int -> item == arr?.get(i)?.entryID }
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
