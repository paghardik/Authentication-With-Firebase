package com.example.authenticationwithfierbase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.example.authenticationwithfierbase.databinding.ActivityEmailPasswordBinding
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class EmailPasswordActivity : BaseActivity(), View.OnClickListener {
    lateinit var binding: ActivityEmailPasswordBinding
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mbCreateAccount.setOnClickListener(this)
        binding.mbLogout.setOnClickListener(this)
        binding.mbSignIn.setOnClickListener(this)

        auth = Firebase.auth
    }

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser?.also {
            binding.mbLogout.visibility = View.VISIBLE
        }

    }

    private fun validateForm(): Boolean {
        var valid = true

        val email = binding.editEmail.text.toString()
        if (TextUtils.isEmpty(email)) {
            binding.editEmail.error = "Required."
            valid = false
        } else {
            binding.editEmail.error = null
        }

        val password = binding.editPassword.text.toString()
        if (TextUtils.isEmpty(password)) {
            binding.editPassword.error = "Required."
            valid = false
        } else {
            binding.editPassword.error = null
        }

        return valid
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.mbCreateAccount -> {
                createAccount(binding.editEmail.text.toString(), binding.editPassword.text.toString())
            }
            R.id.mbLogout -> logOut()
            R.id.mbSignIn -> signIn(binding.editEmail.text.toString(), binding.editPassword.text.toString())
        }
    }

    private fun createAccount(email: String, pass: String) {
        if (!validateForm()){
            return
        }

        auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener { task ->
            if (task.isSuccessful){
                binding.mbLogout.visibility = View.VISIBLE
                val user = auth.currentUser?.let { fbUser ->
                }
            }else{
                Log.e("TAG", "Error msg",task.exception)
            }
        }
    }

    private fun signIn(email: String, pass: String){
        if (!validateForm()){
            return
        }


        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful){
                with(binding){
                    editEmail.text.clear()
                    editPassword.text.clear()
                    mbLogout.visibility = View.VISIBLE
                }
            }


        }
    }

    private fun logOut(){
        auth.signOut()
        binding.mbLogout.visibility = View.INVISIBLE
    }
}

