package dev.panwar.neuroinsight.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import dev.panwar.neuroinsight.R
import dev.panwar.neuroinsight.api.RetrofitInstance
import dev.panwar.neuroinsight.databinding.ActivityLoginBinding
import dev.panwar.neuroinsight.models.request.UsernameLogin
import dev.panwar.neuroinsight.models.response.LoginResponse
import dev.panwar.neuroinsight.utils.Constants
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : BaseActivity() {

    private var binding: ActivityLoginBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupActionBar()

        binding?.tvRegister?.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
        }

        val sharedPreferences=getSharedPreferences(Constants.TOKEN_PREFERENCES,Context.MODE_PRIVATE)
        val authToken=sharedPreferences.getString(Constants.AUTH_TOKEN,"")

        if (authToken!=null){
            if (authToken.isNotEmpty()){
                loginUser()
            }
        }

        binding?.btnLoginIn?.setOnClickListener {
            val email=binding?.etEmailSignIn?.text.toString().trim()
            val password=binding?.etPasswordSignIn?.text.toString().trim()
            val username=binding?.etUsernameSignIn?.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(UsernameLogin(userName = null,password = password,email = email))

            }else if (username.isNotEmpty() && password.isNotEmpty()){
                loginUser(UsernameLogin(userName = username,password = password,email = null))

            }
            else {
                showToast("Please fill required fields")
                return@setOnClickListener
            }

        }

    }

    private fun loginUser(user:UsernameLogin) {
        val loginRequest= UsernameLogin(userName = user.userName,password = user.password,email = user.email)
        showProgressDialog("Please wait...")
        val call: Call<LoginResponse> = RetrofitInstance.api.login(loginRequest = loginRequest)
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                hideProgressDialogue()
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    Log.e("Login Response",response.body().toString())
                    showToast("Login Successful")
                    saveAuthToken(loginResponse?.jwt)
                    hideProgressDialogue()
                    loginUser()
                    finish()
                } else {
                    hideProgressDialogue()
                    val errorBody = response.errorBody()?.string()
                    try {
                        val jsonObject = JSONObject(errorBody)
                        val errorMessage = jsonObject.getString("message")
                        Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_LONG).show()
                        clearFields()
                    } catch (e: Exception) {
                        clearFields()
                        Toast.makeText(this@LoginActivity, errorBody, Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                hideProgressDialogue()
                clearFields()
                Toast.makeText(this@LoginActivity, "Request failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun clearFields() {
        binding?.etEmailSignIn?.setText("")
        binding?.etPasswordSignIn?.setText("")
        binding?.etUsernameSignIn?.setText("")
    }

    private fun loginUser() {
//        Toast.makeText(this@LoginActivity,authToken,Toast.LENGTH_SHORT).show()
        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        finish()
    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarLoginInActivity)
        val actionBar=supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back)

        }
        binding?.toolbarLoginInActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun saveAuthToken(token: String?) {
        // Store the authentication token securely, e.g., using SharedPreferences
        val sharedPreferences = getSharedPreferences(Constants.TOKEN_PREFERENCES, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(Constants.AUTH_TOKEN, token)
        editor.apply()
    }

    override fun onDestroy() {
        binding=null
        super.onDestroy()
    }
}