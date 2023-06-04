package com.mashimur.messagram.data.remote

import com.mashimur.messagram.data.models.MoviesResponseModel
import com.mashimur.messagram.data.models.Results
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieService {
    @GET("3/movie/now_playing")
    suspend fun getCurrentPlayingMovies(@Query("api_key") apiKey: String): MoviesResponseModel


    @GET("3/movie/upcoming")
    suspend fun getUpComingMovies(@Query("api_key") apiKey: String): MoviesResponseModel

    @GET("3/movie/{movieID}")
    suspend fun getMovie(
        @Path("movieID") movieID: String,
        @Query("api_key") apiKey: String
    ): Results


    @GET("3/movie/{movieID}/similar")
    suspend fun getMovieSimilar(
        @Path("movieID") movieID: String,
        @Query("api_key") apiKey: String
    ): MoviesResponseModel
}