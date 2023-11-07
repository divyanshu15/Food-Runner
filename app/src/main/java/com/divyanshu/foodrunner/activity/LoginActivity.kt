package com.divyanshu.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.divyanshu.foodrunner.R
import com.divyanshu.foodrunner.util.ConnectionManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    private val APPLICATION_ID = "com.divyanshu.foodrunner"
    private val PREFS_LOGIN_INSTANCE = "loginPref"
    lateinit var loginProgressBar : ProgressBar
    lateinit var editText_phone: TextInputEditText
    lateinit var editText_pass: TextInputEditText
    lateinit var editText_phone_layout: TextInputLayout
    lateinit var editText_pass_layout: TextInputLayout
    lateinit var button_login: MaterialButton
    lateinit var text_view_signup: TextView
    lateinit var text_view_forgot: TextView
    lateinit var coordinatorViewLogin: CoordinatorLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPref: SharedPreferences =
            this.getSharedPreferences(APPLICATION_ID, Context.MODE_PRIVATE)
        if (!sharedPref.getBoolean(PREFS_LOGIN_INSTANCE, false)) {
            setContentView(R.layout.activity_login)
            editText_phone = findViewById(R.id.editText_phone)
            editText_pass = findViewById(R.id.editText_pass)
            editText_phone_layout = findViewById(R.id.editText_phone_layout)
            editText_pass_layout = findViewById(R.id.editText_pass_layout)
            button_login = findViewById(R.id.button_login)
            text_view_signup = findViewById(R.id.text_view_signup)
            text_view_forgot = findViewById(R.id.text_view_forgot)
            coordinatorViewLogin = findViewById(R.id.coordinatorViewLogin)
            loginProgressBar = findViewById(R.id.loginProgressBar)
            loginProgressBar.visibility = View.GONE
            button_login.setOnClickListener {
                hideKeyboard()
                editText_pass_layout.error = null
                editText_phone_layout.error = null
                if (validate()) {
                    editText_pass_layout.visibility = View.INVISIBLE
                    editText_phone_layout.visibility = View.INVISIBLE
                    button_login.isEnabled = false
                    loginProgressBar.visibility = View.VISIBLE

                    val queue = Volley.newRequestQueue(this@LoginActivity)
                    val url = "http://13.235.250.119/v2/login/fetch_result"

                    val jsonParams = JSONObject()
                    jsonParams.put("mobile_number", editText_phone.text.toString())
                    jsonParams.put("password", editText_pass.text.toString())

                    if (ConnectionManager().checkConnectivity(this@LoginActivity)) {
                        val jsonObjectRequest =
                            object : JsonObjectRequest(
                                Method.POST, url,
                                jsonParams,
                                Response.Listener {
                                    try {
                                        val dataObject = it.getJSONObject("data")
                                        val success = dataObject.getBoolean("success")
                                        if (success) {
                                            val data = dataObject.getJSONObject("data")

                                            val editor: SharedPreferences.Editor = sharedPref.edit()
                                            editor.putBoolean(PREFS_LOGIN_INSTANCE, true)
                                            editor.putString("user_id", data.getString("user_id"))
                                            editor.putString("name", data.getString("name"))
                                            editor.putString("email", data.getString("email"))
                                            editor.putString(
                                                "mobile_number",
                                                data.getString("mobile_number")
                                            )
                                            editor.putString("address", data.getString("address"))
                                            editor.apply()
                                            val intentToMainActivity =
                                                Intent(this@LoginActivity, MainActivity::class.java)
                                            startActivity(intentToMainActivity)
                                            finish()

                                        } else {
                                            loginProgressBar.visibility = View.GONE
                                            editText_pass_layout.visibility = View.VISIBLE
                                            editText_phone_layout.visibility = View.VISIBLE
                                            button_login.isEnabled = true
                                            val errorMessage = dataObject.getString("errorMessage")
                                            Snackbar.make(
                                                coordinatorViewLogin,
                                                errorMessage,
                                                Snackbar.LENGTH_LONG
                                            ).setAnchorView(R.id.text_view_signup).show()
                                        }
                                    } catch (e: JSONException) {
                                        loginProgressBar.visibility = View.GONE
                                        editText_pass_layout.visibility = View.VISIBLE
                                        editText_phone_layout.visibility = View.VISIBLE
                                        button_login.isEnabled = true
                                        Snackbar.make(
                                            coordinatorViewLogin,
                                            "Some unexpected error has occurred while we were handling the data.",
                                            Snackbar.LENGTH_LONG
                                        ).setAnchorView(R.id.text_view_signup).show()
                                    }
                                },
                                Response.ErrorListener {
                                    loginProgressBar.visibility = View.GONE
                                    editText_pass_layout.visibility = View.VISIBLE
                                    editText_phone_layout.visibility = View.VISIBLE
                                    button_login.isEnabled = true
                                    Snackbar.make(
                                        coordinatorViewLogin,
                                        "We failed to fetch the data. Please Retry.",
                                        Snackbar.LENGTH_LONG
                                    ).setAnchorView(R.id.text_view_signup).show()
                                }) {
                                override fun getHeaders(): MutableMap<String, String> {
                                    val headers = HashMap<String, String>()
                                    headers["Content-type"] = "application/json"
                                    headers["token"] = "c0e6793dd5c434"
                                    return headers
                                }
                            }
                        queue.add(jsonObjectRequest)
                    } else {
                        loginProgressBar.visibility = View.GONE
                        editText_pass_layout.visibility = View.VISIBLE
                        editText_phone_layout.visibility = View.VISIBLE
                        button_login.isEnabled = true
                        Snackbar.make(
                            coordinatorViewLogin,
                            "No Internet Connection.",
                            Snackbar.LENGTH_LONG
                        ).setAction("Retry") { button_login.performClick() }
                            .setAnchorView(R.id.text_view_signup).show()
                    }
                } else {
                    if (editText_phone.text?.length != 10) {
                        editText_phone_layout.error = "Incorrect Mobile Number"
                        Snackbar.make(
                            coordinatorViewLogin,
                            "Incorrect Mobile Number",
                            Snackbar.LENGTH_LONG
                        ).setAnchorView(R.id.text_view_signup).show()
                    } else {
                        editText_pass_layout.error = "Improper Password"
                        Snackbar.make(
                            coordinatorViewLogin,
                            "Improper Password",
                            Snackbar.LENGTH_LONG
                        ).setAnchorView(R.id.text_view_signup).show()
                    }
                }
            }
            text_view_signup.setOnClickListener {
                val intentToSignUpActivity = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intentToSignUpActivity)
            }
            text_view_forgot.setOnClickListener {
                val intentToForgotActivity = Intent(this@LoginActivity, ForgotActivity::class.java)
                startActivity(intentToForgotActivity)
            }
        } else {
            val intentToMainActivity = Intent(this@LoginActivity, MainActivity::class.java)
            intentToMainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intentToMainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intentToMainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intentToMainActivity.putExtra("EXIT", true)
            startActivity(intentToMainActivity)
            finish()
        }
    }

    private fun validate(): Boolean {
        return editText_phone.text?.length == 10 && editText_pass.text!!.length > 5
    }

    private fun hideKeyboard() {
        val view = currentFocus
        if (view != null && view.windowToken != null) {
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }
}