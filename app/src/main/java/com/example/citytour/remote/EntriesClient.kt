package com.example.citytour.remote

import com.example.citytour.model.EntriesRequestResponse
import retrofit2.Response
import retrofit2.http.GET

interface EntriesClient {
    @GET("entries")
    suspend fun getEntries(): Response<EntriesRequestResponse>
}