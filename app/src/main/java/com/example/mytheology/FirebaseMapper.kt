package com.example.mytheology

import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.*


class FirebaseMapper {

    var database: DatabaseReference = FirebaseDatabase.getInstance().reference
    val sectionReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("section")

    fun newSection(obj:MainModel) {
        val newSection = database.child("section").push()
        obj.UID = newSection.key
        newSection.setValue(obj)
    }

    fun saveEntry(entryId:String, sectionID:String, text:String, title:String){
        sectionReference.child(sectionID.toString()).child("entries").child(entryId).child("entry").setValue(text)
        sectionReference.child(sectionID.toString()).child("entries").child(entryId).child("title").setValue(title)
    }



}