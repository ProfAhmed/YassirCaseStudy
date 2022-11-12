package com.osama.pro.app.detail

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.osama.pro.app.R
import com.osama.pro.app.databinding.ActivityDetailBinding
import com.osama.pro.app.state.UiState
import com.osama.pro.app.utils.AppPreferences
import com.osama.pro.app.utils.Event
import com.osama.pro.core.domain.model.DomainModel
import com.osama.pro.core.domain.model.Movie
import com.osama.pro.core.extension.glideImageWithOptions
import com.osama.pro.core.extension.invisible
import com.osama.pro.core.extension.showSnackbar
import com.osama.pro.core.extension.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {
    private val binding by lazy { ActivityDetailBinding.inflate(layoutInflater) }
    private val content by lazy { binding.content }
    private val viewModel: DetailViewModel by viewModels()

    @Inject
    lateinit var appPreferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        enableBackButton()
        observeState()
        lifecycleScope.launchWhenCreated {
            viewModel.action.emit(DetailAction.FetchDetails)
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        UiState.Idle -> {
                        }
                        UiState.Loading -> handleLoading(state is UiState.Loading)
                        is UiState.Error -> handleError(state.throwable)
                        is UiState.Success -> {
                            handleLoading(state is UiState.Loading)
                            when (state.data) {
                                is Movie -> renderMovieDetails(state.data)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun observeFavoriteState(title: String) {
        viewModel.favoriteState.observe(this) { itemIsFavorite ->
            val buttonText = if (itemIsFavorite)
                R.string.remove_from_my_list
            else
                R.string.add_to_my_list
            content.btnFavorite.text = getString(buttonText)
        }
        viewModel.snackBarText.observe(this) { event ->
            showSnackbar(event, title)
        }
    }

    private fun showSnackbar(event: Event<Int>, title: String) {
        val message = event.getContent() ?: return
        content.root.showSnackbar(getString(message, title))
    }

    private fun renderMovieDetails(movie: Movie) {
        observeFavoriteState(movie.title)
        bindToView(movie)
    }

    private fun bindToView(movie: Movie) {
        val imageSize = appPreferences.getImageSize()
        val backdropUrl = movie.getBackdropUrl(imageSize)
        val posterUrl = movie.getPosterUrl(imageSize)
        bind(backdropUrl, posterUrl, movie)

        with(content) {
            tvTitle.text = movie.title
            tvOverview.text = movie.overview
            ratingBar.rating = movie.voteAverage.toFloat()
                .div(2)
            tvRating.text = movie.voteAverage.toString()
            tvRuntime.text = getString(R.string.runtime, movie.runtime)
            tvDirector.text = movie.director
            tvGenre.text = movie.genres
            tvReleaseDate.text = movie.releaseDate
        }
    }

    private fun bind(backdropUrl: String, posterUrl: String, model: DomainModel) = with(binding) {
        imgCover.glideImageWithOptions(backdropUrl)
        with(content) {
            imgPoster.glideImageWithOptions(posterUrl)
            btnFavorite.setOnClickListener { viewModel.toggleFavoriteState(model) }
            btnFavorite.isEnabled = true
        }
    }

    private fun handleError(throwable: Throwable) = Timber.e(throwable)

    private fun handleLoading(isLoading: Boolean) = with(content) {
        if (isLoading) {
            details.invisible()
            shimmerContainer.startShimmer()
        } else {
            shimmerContainer.stopShimmer()
            shimmerContainer.invisible()
            details.visible()
        }
    }

    private fun enableBackButton() = supportActionBar?.apply {
        setDisplayHomeAsUpEnabled(true)
        setDisplayShowHomeEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}