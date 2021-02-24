package com.example.mytheology

import android.widget.TextView

interface UpdateAndDelete {

    fun modifyItem(ItemUID:String, isDone:Boolean)
    fun onItemDelete(itemUID:String)
    fun onSectionClick(ItemUID:String, title:String)

}