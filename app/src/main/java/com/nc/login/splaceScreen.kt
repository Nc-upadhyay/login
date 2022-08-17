package com.nc.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ProgressBar
import com.google.firebase.database.core.Context

class splaceScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splace_screen)
        var progressBar:ProgressBar=findViewById(R.id.progress_bar)
        Handler().postDelayed(Runnable {
            progressBar.visibility= View.INVISIBLE
            startActivity(Intent(this,MainActivity::class.java))
        },3000)
    }
}