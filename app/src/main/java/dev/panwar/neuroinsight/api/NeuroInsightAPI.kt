package dev.panwar.neuroinsight.api

import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import dev.panwar.neuroinsight.models.request.PersonalDetailsRequest
import dev.panwar.neuroinsight.models.request.QuestionResponses
import dev.panwar.neuroinsight.models.request.SignupRequest
import dev.panwar.neuroinsight.models.request.UsernameLogin
import dev.panwar.neuroinsight.models.response.GetGADQuestionsResponse
import dev.panwar.neuroinsight.models.response.LoginResponse
import dev.panwar.neuroinsight.models.response.PersonalDetailsResponse
import dev.panwar.neuroinsight.models.response.PersonalDetailsResponseItem
import dev.panwar.neuroinsight.models.response.User
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface NeuroInsightAPI {

    @POST("/public/signup")
    fun signup(@Body signupRequest: SignupRequest): Call<User>

    @POST("/public/login")
    fun login(@Body loginRequest: UsernameLogin): Call<LoginResponse>

    @GET("/user/personal-details")
    fun getPersonalDetails(@Header("Authorization") authToken:String): Call<PersonalDetailsResponse>

    @POST("/user/personal-details/addResponse")
    fun postPersonalDetails(@Header("Authorization") authToken:String,@Body personalDetails: PersonalDetailsRequest): Call<ResponseBody>

    @GET("/user/gad/questions")
    fun getGADQuestions(@Header("Authorization") authToken:String): Call<GetGADQuestionsResponse>

    @GET("/user/phq/questions")
    fun getFHQQuestions(@Header("Authorization") authToken:String): Call<GetGADQuestionsResponse>


    @POST("/user/gad/addResponses")
    fun postGADResponses(@Header("Authorization") authToken:String,@Body responses: List<QuestionResponses>): Call<ResponseBody>

    @POST("/user/phq/addResponses")
    fun postFHQResponses(@Header("Authorization") authToken:String,@Body responses: List<QuestionResponses>): Call<ResponseBody>





}