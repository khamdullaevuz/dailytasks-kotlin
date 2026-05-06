package uz.esoft.dailytasks.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitProvider {
    fun createMockTasksApi(): TasksApi {
        val client = OkHttpClient.Builder()
            .addInterceptor(MockApiInterceptor())
            .build()

        val retrofit = Retrofit.Builder()
            // Base URL is required but not used by the interceptor.
            .baseUrl("https://mock.api/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(TasksApi::class.java)
    }
}

