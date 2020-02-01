package org.techtown.kotlin_todolist

import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.techtown.dailypicture.Retrofit.Request.LoginRequest
import org.techtown.dailypicture.Retrofit.Request.PostRequest
import org.techtown.dailypicture.Retrofit.Request.RegisterRequest
import org.techtown.dailypicture.Retrofit.Response.*
import org.techtown.dailypicture.utils.TokenTon

import retrofit2.Call
import retrofit2.http.*
import java.io.File

interface RetrofitService {
    @Headers("Accept: application/json")
    @POST("/accounts/")    //사용자의 id,username을 반환
    //fun registerUser(@Body user : RegisterRequest) : Call<RegisterUserResponse>
    fun registerUser(@Body post: RegisterRequest):Call<RegisterResponse>

    //username,password주면 token값 반환
    @Headers("Accept: application/json")
    @POST("/api-token-auth/")
    fun getToken(@Body user : LoginRequest) : Call<LoginResponse>

    @Headers("Accept:application/json")
    @Multipart
    @POST("/posts/")
    fun registerPost(@Part("title") title:RequestBody,@Part thumbnail:MultipartBody.Part, @Header("Authorization")authorization:String):Call<PostResponse>


    @Headers("Accept:application/json")
    @GET("/posts/")
    fun getPost(@Header("Authorization")authorization:String):Call<List<PostResponse>>

    @Headers("Accept:application/json")
    @Multipart
    @POST("/images/create")
    fun imagePost(@Part("post") post:RequestBody,@Part url:MultipartBody.Part,@Header("Authorization")authorization:String):Call<ImagePostResponse>


    @Headers("Accept:application/json")
    @GET("/posts/{post_id}")
    fun postIdPost(@Header("Authorization")authorization:String,@Path("post_id") post_id:Int?):Call<PostIdResponse>

    @Headers("Accept:application/json")
    @DELETE("/posts/{post_id}/")
    fun postIdDelete(@Header("Authorization")authorization:String,@Path("post_id") post_id:Int?):Call<PostIdResponse>

    @Headers("Accept:application/json")
    @GET("/videos/{id}/video")
    fun getVideo(@Header("Authorization")authorization:String,@Path("id") id:Int?):Call<VideoResponse>

    @Headers("Accept:application/json")
    @GET("/videos/{post_id}/gif")
    fun getGif(@Header("Authorization")authorization:String,@Path("post_id") id:Int?):Call<VideoResponse>

    @Headers("Accept:application/json")
    @DELETE("/images/{id}")
    fun imageDelete(@Header("Authorization")authorization:String,@Path("id") id:Int?):Call<PostIdResponse>
}