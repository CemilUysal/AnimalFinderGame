package com.example.animalfinder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }
    fun onclick_newgame(view: View){
        val intent = Intent(this, FirstLevel::class.java)
        startActivity(intent)
    }

}