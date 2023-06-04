package com.mashimur.messagram.data.repositories

import com.mashimur.messagram.data.models.MoviesResponseModel
import com.mashimur.messagram.data.models.Results


interface MoviesRepository {
    suspend fun getCurrentPlayingMovies(): MoviesResponseModel
    suspend fun getUpComingMovies(): MoviesResponseModel
    suspend fun getMovie(movieID: String): Results
    suspend fun getMovieSimilar(movieID: String): MoviesResponseModel
}