package me.tylerbwong.apollorootqueryfragmentbug

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import okhttp3.OkHttpClient
import okio.Buffer

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor {
                try {
                    val request = it.request().newBuilder().build()
                    val buffer = Buffer()
                    request.body?.writeTo(buffer)
                    Log.d("APOLLO_REQUEST_BODY", buffer.readUtf8())
                } catch (ex: Exception) {
                    Log.e("OKHTTP_ERROR", "Could not read request body.")
                }
                it.proceed(it.request())
            }
            .build()

        val apolloClient = ApolloClient.builder()
            .okHttpClient(okHttpClient)
            .serverUrl("https://swapi.apis.guru/")
            .build()

        apolloClient
            .query(PlanetRootQueryBugQuery())
            .enqueue(
                object : ApolloCall.Callback<PlanetRootQueryBugQuery.Data>() {
                    override fun onResponse(response: Response<PlanetRootQueryBugQuery.Data>) {
                        Log.d("APOLLO_RESPONSE", "${response.data}")
                    }

                    override fun onFailure(e: ApolloException) {
                        Log.e("APOLLO_ERROR", e.message ?: "$e")
                    }
                }
            )

        apolloClient
            .query(PlanetRootQueryWorksQuery())
            .enqueue(
                object : ApolloCall.Callback<PlanetRootQueryWorksQuery.Data>() {
                    override fun onResponse(response: Response<PlanetRootQueryWorksQuery.Data>) {
                        Log.d("APOLLO_RESPONSE", "${response.data}")
                    }

                    override fun onFailure(e: ApolloException) {
                        Log.e("APOLLO_ERROR", e.message ?: "$e")
                    }
                }
            )
    }
}
