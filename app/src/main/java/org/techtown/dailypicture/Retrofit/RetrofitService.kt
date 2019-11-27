package org.techtown.kotlin_todolist

import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.techtown.dailypicture.Retrofit.Request.LoginRequest
import org.techtown.dailypicture.Retrofit.Request.PostRequest
import org.techtown.dailypicture.Retrofit.Request.RegisterRequest
import org.techtown.dailypicture.Retrofit.Response.PostResponse
import org.techtown.dailypicture.Retrofit.Response.LoginResponse
import org.techtown.dailypicture.Retrofit.Response.RegisterResponse

import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {
    @Headers("Accept: application/json")
    @POST("/accounts/")    //사용자의 id,username을 반환
    //fun registerUser(@Body user : RegisterRequest) : Call<RegisterUserResponse>
    fun registerUser(@Body post: RegisterRequest):Call<RegisterResponse>

    //username,password주면 token값 주는 것
    @Headers("Accept: application/json")
    @POST("/api-token-auth/")
    fun getToken(@Body user : LoginRequest) : Call<LoginResponse>


    @Headers("Accept:application/json")
    @Multipart
    @POST("/posts/")
    fun registerPost(@Part("title") title:RequestBody,@Part thumbnail:MultipartBody.Part, @Header("Authorization")authorization:String):Call<PostResponse>
    //fun registerPost(@Part("title") title:String,@Part("thumbnail") thumbnail:MultipartBody.Part, @Header("Authorization")authorization:String):Call<PostResponse>
    //fun registerPost(@Body post:PostRequest, @Header("Authorization")authorization:String):Call<PostResponse>

    @Headers("Accept:application/json")
    @GET("/posts/")
    fun getPost(@Header("Authorization")authorization:String):Call<List<PostResponse>>

}