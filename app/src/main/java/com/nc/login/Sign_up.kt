package com.nc.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.database.*
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates


class Sign_up : Fragment(R.layout.fragment_sing_up) {
    lateinit var name_box: EditText
    lateinit var password_box: EditText
    lateinit var number_box: EditText
    lateinit var otp_edit: EditText
    lateinit var sing_up_button: Button
    lateinit var getotp: Button
    lateinit var otp_code: String
    var varification_flag: Int = 0

    lateinit var name: String
    lateinit var password: String
    lateinit var number: String

    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var databaseReference: DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    var TAG: String = "Sign_UP"
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_sing_up, null)

        firebaseDatabase = FirebaseDatabase.getInstance()
        sing_up_button = view.findViewById(R.id.Sing_up)
        password_box = view.findViewById(R.id.Password)
        number_box = view.findViewById(R.id.phone_number)
        name_box = view.findViewById(R.id.name)
        getotp = view.findViewById(R.id.getOtp);
        otp_edit = view.findViewById(R.id.otp);
        auth = FirebaseAuth.getInstance()
        //  initialize_Variable()
        sing_up_button.visibility = View.INVISIBLE;
        sing_up_button.setOnClickListener {

            initialize_Variable()
            var code: String = otp_edit.text.toString().trim()
            if (varification_flag == 1) {
                addData()
            } else {
                println("=================================else")
                val credential = PhoneAuthProvider.getCredential(otp_code!!, code)
                signInWithPhoneAuthCredential(credential)
                addData()
            }

            if (varification_flag == 1)
                addData()
        }
        getotp.setOnClickListener {

            number = "+91${number_box.text.toString().trim()}"
            if (number.isEmpty())
                Toast.makeText(requireContext(), "Mobile number can't be empty", Toast.LENGTH_SHORT)
                    .show()
            else {
                sing_up_button.visibility = View.VISIBLE;
                getotp.visibility = View.INVISIBLE
                initialize_callback()

                val options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(number)       // Phone number to verify
                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                    .setActivity(requireActivity())                 // Activity (for callback binding)
                    .setCallbacks(callbacks)         // OnVerificationStateChangedCallbacks
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)
            }
        }

        return view;
        //  return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun initialize_callback() {
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e)
                Toast.makeText(requireContext(), "Verification Failed", Toast.LENGTH_SHORT).show()
                println("Otp is fail================================")
                if (e is FirebaseAuthInvalidCredentialsException) {
                    Log.d(TAG, "=========== ${e.localizedMessage}")
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }

                // Show a message and update the UI
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Toast.makeText(requireContext(), "OnCode Sent call", Toast.LENGTH_SHORT).show()
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them later
                otp_code = verificationId

            }
        }

    }

    private fun addData() {
        initialize_Variable()
        var user: User = User(name, password, number)

        databaseReference = firebaseDatabase.getReference("User_Data")
        databaseReference.child(number).setValue(user).addOnSuccessListener {
//            Toast.makeText(requireContext(), "Successfully Saved Data" , Toast.LENGTH_SHORT).show()
            password_box.setText("")
            name_box.setText("")
            number_box.setText("")
           // alertDialog("Data Added")
            goto_DeskBoard()

        }.addOnFailureListener {

            Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()


        }


    }

    private fun initialize_Variable() {
        name = name_box.text.toString().trim();
        password = password_box.text.toString().trim();
        number=number_box.text.toString().trim();
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        Log.d(TAG, "===========================================");
        Log.d(TAG, "signInWithPhoneAuthCredential is calll ");
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    println("Sign in completed")
                    varification_flag = 1
                    otp_edit.focusable=View.NOT_FOCUSABLE
                    alertDialog("Authentication Completed")
//                    Toast.makeText(requireContext(), "verificatin Completed", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "signInWithCredential:success====================")

                    val user = task.result?.user
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Log.d(TAG, "===========================================");
                        Log.d(TAG, "Some Erro is Occured ");
                    }
                    // Update UI
                }
            }
    }

    fun alertDialog(msg:String)
    {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Message")
        builder.setMessage(msg)
//builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            Toast.makeText(requireContext(),
                android.R.string.yes, Toast.LENGTH_SHORT).show()
        }



        builder.show()
    }

    fun goto_DeskBoard()
    {
        var intent =Intent(context,Desk::class.java)
        startActivity(intent)
    }

}