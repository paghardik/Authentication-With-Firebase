package com.example.authenticationwithfierbase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.authenticationwithfierbase.databinding.ActivityFacebookLoginBinding
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class FacebookLoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityFacebookLoginBinding

    private lateinit var auth: FirebaseAuth

    private var callbackManager: CallbackManager = CallbackManager.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFacebookLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.buttonFacebookSignout.setOnClickListener(this)

        auth = Firebase.auth

        binding.buttonFacebookLogin.setPermissions("email", "public_profile")
        binding.buttonFacebookLogin.registerCallback(callbackManager, object : FacebookCallback<LoginResult>{
            override fun onSuccess(result: LoginResult?) {
                result?.let { handleFacebookAccessToken(it.accessToken) }
            }

            override fun onCancel() {
                updateUI(null)
            }

            override fun onError(error: FacebookException?) {
                updateUI(null)
            }

        })

    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun signOut() {
        auth.signOut()
        LoginManager.getInstance().logOut()

        updateUI(null)
    }

    private fun handleFacebookAccessToken(token: AccessToken) {

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }
    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            binding.status.text = user.displayName
            binding.detail.text = getString(R.string.firebase_status_fmt, user.uid)

            binding.buttonFacebookLogin.visibility = View.GONE
            binding.buttonFacebookSignout.visibility = View.VISIBLE
        } else {
            binding.status.setText("SignOut")
            binding.detail.text = null

            binding.buttonFacebookLogin.visibility = View.VISIBLE
            binding.buttonFacebookSignout.visibility = View.GONE
        }
    }


    override fun onClick(v: View?) {
        when(v?.id){
            R.id.buttonFacebookSignout -> signOut()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)

    }
}