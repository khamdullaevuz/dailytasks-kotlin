package uz.esoft.dailytasks.data.remote

import retrofit2.http.GET

interface TasksApi {
    @GET("tasks")
    suspend fun getTasks(): List<TaskDto>
}

