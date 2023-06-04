package com.mashimur.messagram.ui.home

import android.os.Build
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.mashimur.messagram.R
import com.mashimur.messagram.data.models.MoviesResponseModel
import com.mashimur.messagram.ui.home.adapters.NowPlayingSliderAdapter
import com.mashimur.messagram.ui.home.adapters.UpComingMoviesAdapter
import com.mashimur.messagram.utils.Status

import com.mashimur.messagram.databinding.FragmentHomeMoviesBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragmentMovies : Fragment() {
    private var _binding: FragmentHomeMoviesBinding? = null
    private val binding get() = _binding!!
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeMoviesBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val homeViewModel: HomeViewModel by viewModels()
    lateinit var upComingMoviesAdapter: UpComingMoviesAdapter
    lateinit var nowPlayingSliderAdapter: NowPlayingSliderAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupUI()
        setupObservers()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setupUI() {
        upComingMoviesAdapter = UpComingMoviesAdapter(null, requireContext())
        binding.upComingRecyclerView.apply {
            setHasFixedSize(true)
            adapter = upComingMoviesAdapter
        }
        nowPlayingSliderAdapter = NowPlayingSliderAdapter(null, requireContext())
        binding.nowPlayingSlider.apply {
            adapter = nowPlayingSliderAdapter
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    addIndicatorView(position)
                }
            })
        }

    }

    private fun setupObservers() {
        homeViewModel.getCurrentPlayingMovies().observe(viewLifecycleOwner) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        binding.moviesProgressBar.visibility = View.GONE
                        resource.data?.let { movieResponse -> updateSliderData(movieResponse = movieResponse) }
                    }
                    Status.ERROR -> {
                        binding.moviesProgressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                    Status.LOADING -> {
                        binding.moviesProgressBar.visibility = View.VISIBLE
                    }
                }
            }
        }
        homeViewModel.getUpComingMovies().observe(viewLifecycleOwner) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        binding.moviesProgressBar.visibility = View.GONE
                        resource.data?.let { movieResponse -> updateUpComingMoviesList(movieResponse = movieResponse) }
                    }
                    Status.ERROR -> {
                        binding.moviesProgressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                    Status.LOADING -> {
                        binding.moviesProgressBar.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun updateUpComingMoviesList(movieResponse: MoviesResponseModel) {
        upComingMoviesAdapter.updateMoviesModel(moviesModel = movieResponse)
    }

    private fun updateSliderData(movieResponse: MoviesResponseModel) {
        nowPlayingSliderAdapter.updateMoviesModel(moviesModel = movieResponse)

        if (movieResponse.results.isNotEmpty()) {

            lifecycleScope.launch {
                while (findNavController().currentDestination!!.label == "fragment_home_movies") {
                    for (i in 0..movieResponse.results.size) {
                        delay(1500)
                        if(findNavController().currentDestination!!.label != "fragment_home_movies"){
                            break
                        }
                        if (i == 0) {
                            binding.nowPlayingSlider.setCurrentItem(i, false)
                        } else {
                            binding.nowPlayingSlider.setCurrentItem(i, true)
                        }
                    }
                }
            }
        }

    }

    private fun addIndicatorView(currentPage: Int) {
        binding.tabLayoutIndCatorLayout.removeAllViews()

        nowPlayingSliderAdapter.moviesModel?.let {
            for (i in 0..it.results.size) {
                val indicator = TextView(requireContext())

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    indicator.text = Html.fromHtml("&#8226", Html.FROM_HTML_MODE_LEGACY)
                } else {
                    indicator.text = Html.fromHtml("&#8226")
                }

                indicator.textSize = 38f
                if (currentPage == i) {
                    indicator.setTextColor(ContextCompat.getColor(requireContext(), R.color.white_30))
                } else {
                    indicator.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                }

                binding.tabLayoutIndCatorLayout.addView(indicator)
            }
        }

    }

}