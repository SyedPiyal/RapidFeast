package com.piyal.rapidfeast.di

import android.content.Context
import com.airbnb.lottie.BuildConfig
import com.piyal.rapidfeast.data.local.PreferencesHelper
import com.piyal.rapidfeast.data.retrofit.AuthInterceptor
import com.piyal.rapidfeast.data.retrofit.CustomApi
import com.piyal.rapidfeast.data.retrofit.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideAuthInterceptor(context: Context, preferences: PreferencesHelper): AuthInterceptor {
        return AuthInterceptor(context, preferences)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val builder = OkHttpClient()
            .newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)

        if (BuildConfig.DEBUG) {
            val requestInterceptor =
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            builder.addNetworkInterceptor(requestInterceptor)
        }
        return builder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.CUSTOM_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideUserRepository(services: CustomApi): UserRepository {
        return UserRepository(services)
    }

    @Provides
    @Singleton
    fun provideCustomApi(retrofit: Retrofit): CustomApi {
        return retrofit.create(CustomApi::class.java)
    }

    @Provides
    @Singleton
    fun providePreferencesHelper(context: Context): PreferencesHelper {
        return PreferencesHelper(context)
    }


}
