package com.example.myattendance.src

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.RemoteViews
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.biometric.BiometricPrompt
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.myattendance.R
import com.example.myattendance.utility.Biometric
import com.example.myattendance.utility.Registration
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var bottomNavigationView: BottomNavigationView? = null
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    private val mOnNavigationItemSelectedListener: BottomNavigationView.OnNavigationItemSelectedListener = object : BottomNavigationView.OnNavigationItemSelectedListener {
        override fun onNavigationItemSelected(@NonNull item: MenuItem): Boolean {
            var fragment: Fragment
            when (item.itemId) {
                R.id.navigationMyProfile ->
                    return true
                R.id.navigationPending -> {
                    val intent = Intent(baseContext, Pending::class.java)
                    startActivity(intent)
                    return true
                }
                R.id.history -> {
                    val intent = Intent(baseContext, History::class.java)
                    startActivity(intent)
                    return true
                }
                R.id.navigationHome -> return true
            }
            return false
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDarkMode(window)
        setContentView(R.layout.main_menu_side_drawer)

        val userApp = Registration()
        val userBio = Biometric()
        biometricPrompt = userBio.createBiometricPrompt(this, applicationContext, "register")
        promptInfo = userBio.createPromptInfo(getString(R.string.prompt_info_title), "This is for user registration", "Please verify your identity to register", getString(R.string.prompt_info_use_app_password))
        //biometricPrompt.authenticate(promptInfo)

        if(GoogleSignIn.getLastSignedInAccount(applicationContext) != null) {
            if (!userApp.hasRegistered(applicationContext)) {
                biometricPrompt.authenticate(promptInfo)
                //userApp.register(applicationContext)
            }
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<NavigationView>(R.id.nav_view)

        navigationView.setNavigationItemSelectedListener(this)
        bottomNavigationView = findViewById(R.id.navigation)
        bottomNavigationView?.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        //
        val layoutParams: CoordinatorLayout.LayoutParams = bottomNavigationView?.layoutParams as CoordinatorLayout.LayoutParams
        layoutParams.behavior = BottomNavigationBehaviour()
        bottomNavigationView?.selectedItemId = R.id.navigationHome

        //handling floating action menu
        findViewById<FloatingActionButton>(R.id.floatingActionButton).setOnClickListener {
            val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
            drawer.openDrawer(GravityCompat.START)
        }


        val navHeader = navigationView.getHeaderView(0)

        val userAcc = GoogleSignIn.getLastSignedInAccount(applicationContext)
        if (userAcc != null) {
            Picasso.get().load(userAcc.photoUrl).into(navHeader.findViewById<ImageView>(R.id.acc_profilePic))
            navHeader.findViewById<TextView>(R.id.acc_email).text = userAcc.email
            navHeader.findViewById<TextView>(R.id.acc_username).text = userAcc.displayName
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

        // Build a GoogleSignInClient with the options specified by gso.
        var mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        navHeader.findViewById<Button>(R.id.sign_out_button).setOnClickListener {
            signOut(mGoogleSignInClient)
        }

    }

    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {
            }
            R.id.nav_slideshow -> {
            }
            R.id.nav_manage -> {
            }
            R.id.nav_share -> {
            }
            R.id.nav_dark_mode -> {
                //code for setting dark mode
                //true for dark mode, false for day mode, currently toggling on each click
                val darkModePrefManager = DarkModePrefManager(this)
                darkModePrefManager.setDarkMode(!darkModePrefManager.isNightMode)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                recreate()
            }
        }
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    //create a separate class file, if required in multiple activities
    private fun setDarkMode(window: Window) {
        if (DarkModePrefManager(this).isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            changeStatusBar(MODE_DARK, window)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            changeStatusBar(MODE_LIGHT, window)
        }
    }

    private fun changeStatusBar(mode: Int, window: Window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = this.resources.getColor(R.color.contentStatusBar)
            //Light mode
            if (mode == MODE_LIGHT) {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    private fun signOut(Client: GoogleSignInClient) {
        Client.signOut()
                .addOnCompleteListener(this, OnCompleteListener<Void?> {
                    val intent = Intent(this, Login::class.java)
                    startActivity(intent)
                })
    }

    companion object {
        private const val MODE_DARK = 0
        private const val MODE_LIGHT = 1
    }

    fun selectClass(view: View) {
        val intent = Intent(this, ClassSelection::class.java)
        startActivity(intent)
    }

    fun scanQr(view: View) {
        val intent = Intent(this, ScanQr::class.java)
        startActivity(intent)
    }

}