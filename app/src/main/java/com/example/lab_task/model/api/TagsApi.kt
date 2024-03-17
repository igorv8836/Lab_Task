package com.example.lab_task.model.api

import com.example.lab_task.model.api.entities.RegisterUser
import com.example.lab_task.model.api.entities.TagResponse
import com.example.lab_task.model.api.entities.UserResponse
import com.example.lab_task.model.UserAuth
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface TagsApi {
    @GET("/api/tags/")
    suspend fun getTags(
        @Header("Authorization") token: String
    ): Response<List<TagResponse>>

    @Multipart
    @POST("/api/tags/")
    suspend fun addTag(
        @Part("latitude") latitude: Double,
        @Part("longitude") longitude: Double,
        @Part("description") description: RequestBody,
        @Part image: MultipartBody.Part?,
        @Header("Authorization") token: String
    ): Response<TagResponse>


    @DELETE("api/tags/{tag_id}")
    suspend fun deleteTag(
        @Path("tag_id") id: String,
        @Header("Authorization") token: String
    ): Response<String>

    @POST("/api/tags/{tag_id}/likes")
    suspend fun likeTag(
        @Path("tag_id") id: String,
        @Header("Authorization") token: String
    ): Response<TagResponse>

    @DELETE("/api/tags/{tag_id}/likes")
    suspend fun deleteLike(
        @Path("tag_id") id: String,
        @Header("Authorization") token: String
    ): Response<String>


    @POST("/api/auth/register")
    suspend fun registerAccount(
        @Body request: RegisterUser
    ): Response<UserResponse>


    @FormUrlEncoded
    @POST("/api/auth/jwt/login")
    suspend fun authUser(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<UserAuth>

    @GET("/storage/{path}")
    suspend fun getPhoto(
        @Path("path") path: String
    ): Response<String>
}