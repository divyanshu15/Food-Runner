package com.divyanshu.foodrunner.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.room.Room
import com.divyanshu.foodrunner.R
import com.divyanshu.foodrunner.database.RestaurantDatabase
import com.divyanshu.foodrunner.fragment.FAQsFragment
import com.divyanshu.foodrunner.fragment.FavoritesFragment
import com.divyanshu.foodrunner.fragment.HomeFragment
import com.divyanshu.foodrunner.fragment.OrderHistoryFragment
import com.divyanshu.foodrunner.fragment.ProfileFragment
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var nav_view: NavigationView
    lateinit var sharedPref: SharedPreferences
    private val PREFS_LOGIN_INSTANCE = "loginPref"
    private val APPLICATION_ID = "com.divyanshu.foodrunner"
    private lateinit var toolbar: Toolbar
    private lateinit var userName: TextView
    private lateinit var userPhone: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref =
            this.getSharedPreferences(APPLICATION_ID, Context.MODE_PRIVATE)
        if (sharedPref.getBoolean(PREFS_LOGIN_INSTANCE, false)) {
            setContentView(R.layout.activity_main)
            nav_view = findViewById(R.id.nav_view)
            toolbar = findViewById(R.id.toolbar)
            userName = nav_view.getHeaderView(0).findViewById(R.id.userName)
            userPhone = nav_view.getHeaderView(0).findViewById(R.id.userPhone)
            toolbarAndNavViewSetup()
            openHomeFragment()
        } else {
            val intentToLoginActivity = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intentToLoginActivity)
            finish()
        }

    }

    private fun toolbarAndNavViewSetup() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        drawerLayout = findViewById(R.id.drawer_layout)
        nav_view = findViewById(R.id.nav_view)
        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@MainActivity,
            drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        nav_view.setCheckedItem(R.id.action_home)
        nav_view.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_home -> {
                    when (supportFragmentManager.findFragmentById(R.id.nav_host_fragment)) {
                        !is HomeFragment -> {
                            openHomeFragment()
                            drawerLayout.closeDrawers()
                        }
                        else -> {
                            drawerLayout.closeDrawers()
                        }
                    }
                }
                R.id.action_favorites -> {
                    when (supportFragmentManager.findFragmentById(R.id.nav_host_fragment)) {
                        !is FavoritesFragment -> {
                            supportFragmentManager.beginTransaction()
                                .replace(
                                    R.id.nav_host_fragment,
                                    FavoritesFragment()
                                )
                                .commit()
                            supportActionBar?.title = "Favorite Restaurants"
                            drawerLayout.closeDrawers()
                        }
                        else -> {
                            drawerLayout.closeDrawers()
                        }
                    }
                }
                R.id.action_profile -> {
                    when (supportFragmentManager.findFragmentById(R.id.nav_host_fragment)) {
                        !is ProfileFragment -> {
                            supportFragmentManager.beginTransaction()
                                .replace(
                                    R.id.nav_host_fragment,
                                    ProfileFragment()
                                )
                                .commit()
                            supportActionBar?.title = "My Profile"
                            drawerLayout.closeDrawers()
                        }
                        else -> {
                            drawerLayout.closeDrawers()
                        }
                    }
                }
                R.id.action_order_history -> {
                    when (supportFragmentManager.findFragmentById(R.id.nav_host_fragment)) {
                        !is OrderHistoryFragment -> {
                            supportFragmentManager.beginTransaction()
                                .replace(
                                    R.id.nav_host_fragment,
                                    OrderHistoryFragment()
                                )
                                .commit()
                            supportActionBar?.title = "Order History"
                            drawerLayout.closeDrawers()
                        }
                        else -> {
                            drawerLayout.closeDrawers()
                        }
                    }
                }
                R.id.action_faq -> {
                    when (supportFragmentManager.findFragmentById(R.id.nav_host_fragment)) {
                        !is FAQsFragment -> {
                            supportFragmentManager.beginTransaction()
                                .replace(
                                    R.id.nav_host_fragment,
                                    FAQsFragment()
                                )
                                .commit()
                            supportActionBar?.title = "Frequently Asked Questions"
                            drawerLayout.closeDrawers()
                        }
                        else -> {
                            drawerLayout.closeDrawers()
                        }
                    }
                }
                R.id.action_logout -> {
                    drawerLayout.closeDrawers()
                    val dialog = AlertDialog.Builder(this@MainActivity)
                    dialog.setTitle("Confirmation")
                    dialog.setMessage("Are you sure that you want to log out?")
                    dialog.setPositiveButton("Yes, Log Out") { _, _ ->
                        val editor: SharedPreferences.Editor = sharedPref.edit()
                        editor.clear()
                        editor.apply()
                        DeleteFavorites(this@MainActivity).execute()
                        val intentToLoginActivity =
                            Intent(this@MainActivity, LoginActivity::class.java)
                        startActivity(intentToLoginActivity)
                        finish()
                    }
                    dialog.setNegativeButton("Never Mind", null)
                    dialog.create().show()
                }
            }
            return@setNavigationItemSelectedListener true
        }

        val userName = nav_view.getHeaderView(0).findViewById<TextView>(R.id.userName)
        val userPhone = nav_view.getHeaderView(0).findViewById<TextView>(R.id.userPhone)
        userName.text = sharedPref.getString("name", "null")
        userPhone.text = "+91 ${sharedPref.getString("mobile_number", "null")}"
    }

    private fun openHomeFragment() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.nav_host_fragment,
                HomeFragment()
            ).commit()
        supportActionBar?.title = "All Restaurants"
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        when (supportFragmentManager.findFragmentById(R.id.nav_host_fragment)) {
            !is HomeFragment -> {
                openHomeFragment()
                drawerLayout.closeDrawers()
                nav_view.setCheckedItem(R.id.action_home)
            }
            else -> super.onBackPressed()
        }
    }

    class DeleteFavorites(val context: Context) :
        AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {
            val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurants-db")
                .build()
            db.restaurantDao().deleteAllRestaurants()
            db.close()
            return null
        }
    }
}