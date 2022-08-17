package com.nc.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*


class Sign_in : Fragment(R.layout.fragment_signin){
    lateinit var  phone_number:EditText
    lateinit var  password: EditText
    lateinit var register:TextView
    lateinit var signin:Button
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var databaseReference: DatabaseReference

    lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        firebaseDatabase =FirebaseDatabase.getInstance()
        databaseReference=firebaseDatabase.getReference("User_Data");

        var view=inflater.inflate(R.layout.fragment_signin,null)
        progressBar= view.findViewById(R.id.progress_bar)
        phone_number=view.findViewById(R.id.number)
        password=view.findViewById(R.id.password_signin)
        signin=view.findViewById(R.id.sing_in);
        register=view.findViewById(R.id.go_to_sign_up);
        register.setOnClickListener {
            goto_registration()
        }

        signin.setOnClickListener {
            progressBar.visibility=View.VISIBLE
            getData();
        }

        Toast.makeText(requireContext(),"number is ${phone_number.text.toString()}  pass ${password.text.toString()}",Toast.LENGTH_LONG).show()
        return view;
    }

    private fun getData() {
        var number:String="${phone_number.text.toString().trim()}"
        var entered_password:String=password.text.toString().trim()
        databaseReference.child(number).get().addOnSuccessListener {
            progressBar.visibility=View.INVISIBLE
            if(it.exists())
            {

                val password_db = it.child("pass").value
                if(password_db!!.equals(entered_password)) {
                    Toast.makeText(requireContext(), "sign in Complete", Toast.LENGTH_LONG).show()
                    var intent = Intent(context,Desk::class.java)
                    startActivity(intent)
                }else
                {
                    Toast.makeText(requireContext(), "Invelid password", Toast.LENGTH_LONG).show()
                }


            }else
            {
                Toast.makeText(requireContext(),"User does not exits",Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun goto_registration() {
        var signup=Sign_up();
        var fragmentManager = getFragmentManager()
        var transaction = fragmentManager!!.beginTransaction()
        transaction.replace(R.id.fragmentContainerView, signup)
        transaction.commit()
    }
}