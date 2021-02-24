package com.example.mytheology

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class CreateEntryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_entry)

        val b = intent.extras

        val actionBar = supportActionBar
        actionBar!!.title = b!!.getString("1")


    }
}