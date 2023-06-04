package com.mashimur.messagram.repositories

import com.mashimur.messagram.utils.Constants
import com.mashimur.messagram.data.remote.MovieService
import com.mashimur.messagram.data.repositories.MoviesRepository
import javax.inject.Inject

class MoviesRepositoryImpl
@Inject constructor(
    private val movieService: MovieService
) : MoviesRepository {
    override suspend fun getCurrentPlayingMovies() = movieService.getCurrentPlayingMovies(apiKey = Constants.API_KEY)
    override suspend fun getUpComingMovies() = movieService.getUpComingMovies(apiKey = Constants.API_KEY);
    override suspend fun getMovie(movieID: String) = movieService.getMovie(movieID = movieID, apiKey = Constants.API_KEY);
    override suspend fun getMovieSimilar(movieID: String) = movieService.getMovieSimilar(movieID = movieID, apiKey = Constants.API_KEY)
}