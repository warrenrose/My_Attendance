package com.example.myattendance.src

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import com.example.myattendance.R
import com.example.myattendance.utility.Biometric
import com.example.myattendance.utility.Registration
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task


class Login : AppCompatActivity() {

    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()


        // Build a GoogleSignInClient with the options specified by gso.
        var mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        findViewById<SignInButton>(R.id.sign_in_button).setOnClickListener {
            signIn(mGoogleSignInClient)
        }

        /*findViewById<Button>(R.id.sign_out_button).setOnClickListener {
            signOut(mGoogleSignInClient)
        }*/
    }

    private fun updateUI(account: GoogleSignInAccount?) {
        val userApp = Registration()
        val userBio = Biometric()
        biometricPrompt = userBio.createBiometricPrompt(this, applicationContext, "login")
        promptInfo = userBio.createPromptInfo(getString(R.string.prompt_info_title), getString(R.string.prompt_info_subtitle), getString(R.string.prompt_info_description), getString(R.string.prompt_info_use_app_password))
        biometricPrompt.authenticate(promptInfo)
        if(account != null) {
            if(!userApp.hasRegistered(applicationContext)) {
                userApp.register(applicationContext)
            }
            findViewById<SignInButton>(R.id.sign_in_button).visibility = View.GONE
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        else {
            findViewById<SignInButton>(R.id.sign_in_button).visibility = View.VISIBLE
        }
    }

    override fun onStart() {
        super.onStart()
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        val account = GoogleSignIn.getLastSignedInAccount(this)
        updateUI(account)

    }

    private fun signIn(Client: GoogleSignInClient) {
        var signInIntent: Intent = Client.signInIntent
        startActivityForResult(signInIntent, 9001)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 9001) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            updateUI(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Message", "signInResult:failed code=" + e.statusCode)
            updateUI(null)
        }
    }

    fun loginSuccess(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    /*private fun signOut(Client: GoogleSignInClient) {
        Client.signOut()
                .addOnCompleteListener(this, OnCompleteListener<Void?> {
                    val intent = Intent(applicationContext, Login::class.java)
                    startActivity(intent)
                })
    }*/

}