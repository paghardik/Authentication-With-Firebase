package com.example.authenticationwithfierbase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.authenticationwithfierbase.databinding.ActivityGoogleSignInBinding
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.lang.Exception

class GoogleSignIn : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityGoogleSignInBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoogleSignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.mbSignIn.setOnClickListener(this)
        binding.mbRevokeAccess.setOnClickListener(this)
        binding.mbSignOut.setOnClickListener(this)

    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUi(currentUser)
    }

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.mbSignIn -> signIn()
            R.id.mbRevokeAccess -> revokeAccess()
            R.id.mbSignOut -> signOut()
        }
    }

    private fun signIn(){
        googleSignInClient.signInIntent.also {
            startActivityForResult(it, RC_SIGN_IN)
        }
    }

    private fun signOut(){
        auth.signOut()
        googleSignInClient.signOut().addOnCompleteListener {
            updateUi(null)
        }
    }

    private fun revokeAccess(){
        auth.signOut()

        googleSignInClient.revokeAccess().addOnCompleteListener {
            updateUi(null)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    binding.mbSignIn.visibility = View.INVISIBLE
                    binding.mbSignOut.visibility = View.VISIBLE
                    binding.mbRevokeAccess.visibility = View.VISIBLE
                    val user = auth.currentUser
                    updateUi(user)

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUi(null)

                }
            }
    }

    private fun updateUi(user: FirebaseUser?) {
        if(user != null){
            binding.mbSignIn.visibility = View.GONE
            binding.mbSignOut.visibility = View.VISIBLE
            binding.mbRevokeAccess.visibility = View.VISIBLE

            binding.tvDisplayName.text = user.displayName
            binding.tvFbUid.text = user.uid

        }else{
            binding.mbSignIn.visibility = View.VISIBLE
            binding.mbSignOut.visibility = View.GONE
            binding.mbRevokeAccess.visibility = View.GONE

            binding.tvDisplayName.text = ""
            binding.tvFbUid.text = ""
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN){
           val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)
                account?.idToken?.let { firebaseAuthWithGoogle(it) }

            }catch (e: Exception){

            }

        }
    }
}