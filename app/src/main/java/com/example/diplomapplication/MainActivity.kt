package com.example.diplomapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.diplomapplication.di.initKoin
import org.koin.android.ext.koin.androidContext

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initKoin {
            androidContext(this@MainActivity)
        }
        setContentView(R.layout.activity_main)
    }
}
