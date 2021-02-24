package com.example.mytheology

import android.widget.TextView

interface UpdateAndDeleteEntry {

    fun modifyItem(ItemUID:String)
    fun onItemDelete(titlename: String)
    fun onSectionClick( entryID: String)

}