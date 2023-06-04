package com.mashimur.messagram.ui.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.mashimur.messagram.data.models.MoviesResponseModel
import com.mashimur.messagram.data.models.Results
import com.mashimur.messagram.databinding.FragmentDetailBinding
import com.mashimur.messagram.ui.detail.adapters.SimilarMoviesAdapter
import com.mashimur.messagram.utils.Status
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val detailViewModel: DetailViewModel by viewModels()
    private val args: DetailFragmentArgs by navArgs()
    lateinit var similarMoviesAdapter: SimilarMoviesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupAdapter()
        setupObservers()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setupAdapter() {
        similarMoviesAdapter = SimilarMoviesAdapter(null, requireContext())
        binding.similarRecyclerView.apply {
            setHasFixedSize(true)
            adapter = similarMoviesAdapter
        }

    }

    private fun setupObservers() {
        detailViewModel.getMovie(movieID = args.movieID).observe(viewLifecycleOwner, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        binding.movieProgressBar.visibility = View.GONE
                        resource.data?.let { movieResponse -> updateUI(movieResponse = movieResponse) }
                    }
                    Status.ERROR -> {
                        binding.movieProgressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                    Status.LOADING -> {
                        binding.movieProgressBar.visibility = View.VISIBLE
                    }
                }
            }
        })
        detailViewModel.getMovieSimilar(movieID = args.movieID).observe(viewLifecycleOwner, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        binding.movieSimilarProgressBar.visibility = View.GONE
                        resource.data?.let { movieResponse -> updateSimilarList(movieResponse = movieResponse) }
                    }
                    Status.ERROR -> {
                        binding.movieSimilarProgressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                    Status.LOADING -> {
                        binding.movieSimilarProgressBar.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun updateSimilarList(movieResponse: MoviesResponseModel) {
        similarMoviesAdapter.updateMoviesModel(moviesModel = movieResponse)
    }

    private fun updateUI(movieResponse: Results) {
        binding.apply {
            currentPointTextView.text = movieResponse.vote_average.toString()
            movieDateTextView.text = movieResponse.release_date
            movieTitleTextView.text = movieResponse.original_title
            movieDescriptionTextView.text = movieResponse.overview

            Glide.with(requireContext())
                .load("https://www.themoviedb.org/t/p/w600_and_h900_bestv2${movieResponse.poster_path}")
                .into(movieBannerImageView)
        }

    }
}