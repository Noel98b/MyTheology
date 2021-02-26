package com.example.mytheology

import android.widget.Button
import android.widget.Toast

class MainModel {

    companion object Factory{
        fun createList(): MainModel= MainModel()
    }

    var UID:String? = null
    var sectionTitle:String? = null
    var done: Boolean? = false
    var entries: ArrayList<Entry>? = null

}

class Entry {
    var title:String? = null
    var entry:String? = null
    var entryID:String? = null
    var commentList : ArrayList<Comment>? = null
}


class Comment {
    var commentID:String? = null
    var comment:String? = null
    var position:Int? = null
}