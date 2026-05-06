package uz.esoft.dailytasks.data.remote

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.time.Instant
import java.time.LocalDate

/**
 * Simple mock API. It intercepts GET /tasks and returns static JSON.
 * This avoids using external services while still exercising Retrofit.
 */
class MockApiInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath

        if (request.method == "GET" && path.endsWith("/tasks")) {
            val now = Instant.now().toEpochMilli()
            val today = LocalDate.now().toEpochDay()
            val json = """
                [
                  {
                    "id": 1,
                    "title": "Mock: Kundalik reja",
                    "description": "Mock API dan keldi",
                    "dueDateEpochDay": $today,
                    "createdAtEpochMs": $now,
                    "completedAtEpochMs": null,
                    "remindAtEpochMs": ${now + 10 * 60 * 1000},
                    "reminderFiredAtEpochMs": null
                  },
                  {
                    "id": 2,
                    "title": "Mock: Hisobot",
                    "description": "Test vazifa",
                    "dueDateEpochDay": ${today + 1},
                    "createdAtEpochMs": $now,
                    "completedAtEpochMs": null,
                    "remindAtEpochMs": null,
                    "reminderFiredAtEpochMs": null
                  }
                ]
            """.trimIndent()

            return Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(json.toResponseBody("application/json".toMediaType()))
                .build()
        }

        return chain.proceed(request)
    }
}

