package com.divyanshu.foodrunner.fragment

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.divyanshu.foodrunner.R

class ProfileFragment : Fragment() {
    private val APPLICATION_ID = "com.divyanshu.foodrunner"
    private lateinit var userName: TextView
    private lateinit var userPhone: TextView
    private lateinit var emailAddress: TextView
    private lateinit var postalAddress: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        userName = view.findViewById(R.id.userName)
        userPhone = view.findViewById(R.id.userPhone)
        emailAddress = view.findViewById(R.id.emailAddress)
        postalAddress = view.findViewById(R.id.postalAddress)
        if (activity != null) {
            val sharedPref: SharedPreferences =
                (context as Activity).getSharedPreferences(APPLICATION_ID, Context.MODE_PRIVATE)
            userName.text = sharedPref.getString("name", "null")
            emailAddress.text = sharedPref.getString("email", "null")
            userPhone.text = sharedPref.getString("mobile_number", "null")
            postalAddress.text = sharedPref.getString("address", "null")
        }
        return view
    }
}