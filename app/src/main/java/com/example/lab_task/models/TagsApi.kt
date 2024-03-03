package com.example.lab_task.models

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface TagsApi {
    @GET("/api/tags/")
    suspend fun getTags(): Response<List<Tag>>

    @Multipart
    @POST("/api/tags/")
    suspend fun addTag(
        @Part("latitude") latitude: Double,
        @Part("longitude") longitude: Double,
        @Part("description") description: String
    ): Response<Tag>


    @DELETE("api/tags/{tag_id}")
    suspend fun deleteTag(
        @Path("tag_id") id: String,
        @Header("Authorization") token: String
    ): Response<String>

    @POST("/api/tags/")
    suspend fun likeTag()

    @DELETE("/api/tags/{tag_id}/likes")
    suspend fun deleteLike()


    @POST("/api/auth/register")
    suspend fun registerAccount(
        @Body request: RegisterUser
    ): Response<User>


    @FormUrlEncoded
    @POST("/api/auth/jwt/login")
    suspend fun authUser(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<UserAuth>

    @Multipart
    @POST("/api/tags/")
    suspend fun addAuthTag(
        @Part("latitude") latitude: Double,
        @Part("longitude") longitude: Double,
        @Part("description") description: String,
        @Header("Authorization") token: String
    ): Response<Tag>

}