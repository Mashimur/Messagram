package com.mashimur.messagram.ui.home.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mashimur.messagram.data.models.MoviesResponseModel
import com.mashimur.messagram.databinding.ItemSliderBinding
import com.mashimur.messagram.ui.home.HomeFragmentMoviesDirections

class NowPlayingSliderAdapter(var moviesModel: MoviesResponseModel?, private val mContext: Context) : RecyclerView.Adapter<NowPlayingSliderAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        moviesModel?.let { models ->
            val model = models.results[position]
            val view = holder.binding

            view.root.setOnClickListener {
                val directions = HomeFragmentMoviesDirections.actionHomeFragmentMoviesToDetailFragment(movieID = model.id.toString())
                it.findNavController().navigate(directions)
            }

            view.sliderTitleTextView.text = model.original_title
            view.sliderSubTitleTextView.text = model.overview

            Glide.with(mContext)
                .load("https://www.themoviedb.org/t/p/w600_and_h900_bestv2${model.poster_path}")
                .into(view.movieImageView)
        }

    }

    fun updateMoviesModel(moviesModel: MoviesResponseModel){
        this.moviesModel = moviesModel
        notifyDataSetChanged()
    }


    class ViewHolder(val binding: ItemSliderBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemSliderBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = moviesModel?.results?.size ?: 0
}