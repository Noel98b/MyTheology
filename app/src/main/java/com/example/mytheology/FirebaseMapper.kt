package com.example.mytheology

import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.*


class FirebaseMapper {

    var database: DatabaseReference = FirebaseDatabase.getInstance().reference

    fun newSection(obj:MainModel) {
        val newSection = database.child("section").push()
        obj.UID = newSection.key
        newSection.setValue(obj)
    }



}