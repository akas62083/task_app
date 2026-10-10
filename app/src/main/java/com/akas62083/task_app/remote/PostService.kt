package com.akas62083.task_app.remote

import com.akas62083.task_app.remote.dto.PostRequest
import com.akas62083.task_app.remote.dto.PostResponse
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging

interface PostService {

    suspend fun getPosts(): String

    suspend fun createPost(postRequest: PostRequest): PostResponse?


}