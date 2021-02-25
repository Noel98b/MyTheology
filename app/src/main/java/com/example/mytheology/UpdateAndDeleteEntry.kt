package com.example.mytheology

import android.widget.TextView

interface UpdateAndDeleteEntry {

    fun onItemDelete(titlename: String)
    fun onSectionClick( entryID: String)

}