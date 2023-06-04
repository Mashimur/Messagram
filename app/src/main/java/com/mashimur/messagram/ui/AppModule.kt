
package com.mashimur.messagram.ui

import com.mashimur.messagram.data.remote.MovieService
import com.mashimur.messagram.data.repositories.MoviesRepository
import com.mashimur.messagram.repositories.MoviesRepositoryImpl
import com.mashimur.messagram.utils.Constants
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
interface AppModule {

    @Binds
    fun bindMoviesRepositoryImpl(movieRepoImp: MoviesRepositoryImpl): MoviesRepository

    companion object {

        @Provides
        fun provideOkHttpClient(): OkHttpClient {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            return OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
        }

        @Provides
        fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .build()

        @Provides
        fun provideApiService(retrofit: Retrofit): MovieService = retrofit.create(MovieService::class.java)
    }
}