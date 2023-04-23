package com.mobilearts.nftworld.data


import com.mobilearts.nftworld.utils.Constants
import com.mobilearts.nftworld.model.RequestModel
import com.mobilearts.nftworld.model.ResponseModel
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    @Headers(
        "Content-Type: application/json",
        "Authorization: Bearer ${Constants.API_KEY}"
    )
    @POST("v1/chat/completions")
    suspend fun getResponse(@Body request: RequestModel): ResponseModel
}