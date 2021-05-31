package com.dongy.rxkotlin_operator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragment = supportFragmentManager.findFragmentById(R.id.contents)
        if (fragment == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.contents, MainFragment.newInstance(), "MainView")
                .commit()
        }

    }
}