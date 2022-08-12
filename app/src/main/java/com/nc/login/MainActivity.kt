package com.nc.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentContainerView

class MainActivity : AppCompatActivity() {
    lateinit var fragment_container: FragmentContainerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fragment_container = findViewById(R.id.fragmentContainerView)
        var signIn = Sign_in();
        var fragmentManager = getFragmentManager()
        var transaction = supportFragmentManager!!.beginTransaction()
        transaction.replace(R.id.fragmentContainerView, signIn)
        transaction.commit()
    }
}