package com.shapcode.lunch.core.api.interceptor

import com.shapcode.lunch.core.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class GlobalValuesInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalRequestUrl = originalRequest.url

        val updatedRequestUrl = originalRequestUrl.newBuilder()
            .addQueryParameter("key", BuildConfig.PLACE_API_KEY)
            .build()

        val updatedRequest = originalRequest.newBuilder()
            .url(updatedRequestUrl)
            .build()

        return chain.proceed(updatedRequest)
    }
}