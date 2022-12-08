package com.example.jettrivia.network

import com.example.jettrivia.model.Question
import com.example.jettrivia.utils.Constants
import retrofit2.http.GET

interface QuestionApi {

    @GET("world.json")
    suspend fun getAllQuestions() : Question
}