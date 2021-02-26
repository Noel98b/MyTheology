package com.example.mytheology

import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.*


class FirebaseMapper {

    var database: DatabaseReference = FirebaseDatabase.getInstance().reference
    val sectionReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("section")
    var result = MainModel()


    fun newSection(obj:MainModel) {
        val newSection = database.child("section").push()
        obj.UID = newSection.key
        newSection.setValue(obj)
    }

    fun saveEntry(entryId:String, sectionID:String, text:String, title:String){
        sectionReference.child(sectionID.toString()).child("entries").child(entryId).child("entry").setValue(text)
        sectionReference.child(sectionID.toString()).child("entries").child(entryId).child("title").setValue(title)
    }

    fun getEntryData(entryId:String, sectionID: String):Entry{
        var curledEntry = Entry()
        sectionReference.child(sectionID).child("entries").child(entryId).addValueEventListener(object :
                ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value != null) {
                    val map = snapshot.value as HashMap<String, String>
                    curledEntry.title = map["title"]
                    curledEntry.entry = map["entry"]
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }

        })
        return curledEntry
    }

    fun onItemDelete(itemUID:String){
        val itemReference = database.child("section").child(itemUID)
        itemReference.removeValue()
    }

    fun onEntryDelete(entryId: String, sectionID: String){
        val itemReference = sectionReference.child(sectionID.toString()).child("entries").child(entryId)
        itemReference.removeValue()
    }

}