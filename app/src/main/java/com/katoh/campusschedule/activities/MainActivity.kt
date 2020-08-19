package com.katoh.campusschedule.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.katoh.campusschedule.R
import com.katoh.campusschedule.fragments.StartFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar_main))

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.container_main, StartFragment())
                .commit()
        }
    }
}
