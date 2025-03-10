package dev.panwar.neuroinsight.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import dev.panwar.neuroinsight.R
import dev.panwar.neuroinsight.api.RetrofitInstance
import dev.panwar.neuroinsight.databinding.ActivitySignUpBinding
import dev.panwar.neuroinsight.models.request.SignupRequest
import dev.panwar.neuroinsight.models.response.User
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : BaseActivity() {

    private var binding: ActivitySignUpBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupActionBar()

        binding?.btnRegister?.setOnClickListener {
            val fullName=binding?.etFullName?.text.toString().trim()
            val userName=binding?.etUserName?.text.toString().trim()
            val email=binding?.etEmail?.text.toString().trim()
            val password=binding?.etPassword?.text.toString().trim()
            if (fullName.isNotEmpty() && userName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()){
                if (!isValidEmail(binding?.etEmail?.text.toString().trim())){
                    showToast("Invalid Email")
                    return@setOnClickListener
                }
                val signUpRequest=SignupRequest(fullName = fullName,userName = userName,email = email,password = password)
                showProgressDialog("Registering. Please wait!")
                val call: Call<User> = RetrofitInstance.api.signup(signupRequest = signUpRequest)
                call.enqueue(object : Callback<User> {
                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        hideProgressDialogue()
                        if (response.isSuccessful) {
                            val registrationResponse = response.body()
                            Log.e("SignUp Response",response.body().toString())
                            showToast("Registration Successful")
                            startActivity(Intent(this@SignUpActivity,LoginActivity::class.java))
                            finish()
                        } else {
                            val errorBody = response.errorBody()?.string()
                            try {
                                val jsonObject = JSONObject(errorBody)
                                val errorMessage = jsonObject.getString("message")
                                Toast.makeText(this@SignUpActivity, errorMessage, Toast.LENGTH_LONG).show()
                            } catch (e: Exception) {

                                Toast.makeText(this@SignUpActivity, errorBody, Toast.LENGTH_LONG).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<User>, t: Throwable) {
                        hideProgressDialogue()
                        Toast.makeText(this@SignUpActivity, "Request failed: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(this@SignUpActivity, "All fields are required", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarSignUpActivity)
        val actionBar=supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back)

        }
        binding?.toolbarSignUpActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onDestroy() {
        binding=null
        super.onDestroy()
    }

}