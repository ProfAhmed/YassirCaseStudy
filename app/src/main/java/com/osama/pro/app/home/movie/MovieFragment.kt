package com.osama.pro.app.home.movie

import android.app.SearchManager
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.osama.pro.app.R
import com.osama.pro.app.databinding.FragmentMovieBinding
import com.osama.pro.app.extension.intentToDetailsActivity
import com.osama.pro.app.state.UiState
import com.osama.pro.app.utils.AppPreferences
import com.osama.pro.core.domain.model.Movie
import com.osama.pro.core.extension.invisible
import com.osama.pro.core.extension.showSnackbar
import com.osama.pro.core.extension.visible
import com.osama.pro.core.presentation.adapter.MoviePagedAdapter
import com.osama.pro.core.utils.ImageSize
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MovieFragment : Fragment(), SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {
    private val viewModel: MovieViewModel by activityViewModels()

    /**
     *  variable to assign binding onCreateView and release on DestroyView
     *  Scoped to the lifecycle of the fragment's view (between onCreateView and onDestroyView)
     */
    private var _binding: FragmentMovieBinding? = null

    /**
     *   using double bang operator to assure variable is not null
     *   even though it is not recommended to use it,
     *   currently using it to avoid leak.
     **/
    private val binding get() = _binding!!

    private var pagingJob: Job? = null

    @Inject
    lateinit var appPreferences: AppPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        setHasOptionsMenu(true)
        _binding = FragmentMovieBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity != null) {
            setupRecyclerView(appPreferences.getImageSize())
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.state.collect { state ->
                        when (state) {
                            UiState.Idle -> {
                                /** Do nothing **/
                            }

                            UiState.Loading -> binding.stateLoading.visible()

                            is UiState.Error -> showError(state.throwable)

                            is UiState.Success -> renderMovies(state.data)
                        }
                    }
                }
            }
        }
    }

    private fun setupRecyclerView(imageSize: ImageSize) = binding.apply {
        rvDiscoverMovie.apply {
            layoutManager = if (resources.configuration.orientation == ORIENTATION_LANDSCAPE) {
                GridLayoutManager(context, 2)
            } else {
                LinearLayoutManager(context)
            }
            adapter = MoviePagedAdapter(imageSize, ::intentToDetailsActivity)
        }
    }

    private suspend fun renderMovies(pagingData: PagingData<Movie>) {
        binding.stateLoading.invisible()
        pagingJob?.cancel()
        pagingJob = viewLifecycleOwner.lifecycleScope.launch {
            (binding.rvDiscoverMovie.adapter as MoviePagedAdapter)
                .submitData(pagingData)
        }
    }

    private fun showError(throwable: Throwable) {
        Timber.d(throwable)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        val searchManager = requireActivity().getSystemService<SearchManager>()
        val searchView = menu.findItem(R.id.search).actionView as SearchView

        with(searchView) {
            setSearchableInfo(searchManager?.getSearchableInfo(requireActivity().componentName))
            queryHint = getString(R.string.hint_search_movie)
            setOnQueryTextListener(this@MovieFragment)
        }

        menu.findItem(R.id.search).setOnActionExpandListener(this)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        query?.let { submittedText ->
            if (submittedText.isBlank() || submittedText.isEmpty()) {
                binding.root.showSnackbar(getString(R.string.message_query_null))
            } else {
                viewModel.query.value = query
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.movieAction.emit(MovieAction.SearchMovie)
                }
            }
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean = false

    override fun onMenuItemActionExpand(p0: MenuItem?): Boolean = true

    override fun onMenuItemActionCollapse(menuItem: MenuItem?): Boolean {
        lifecycleScope.launch {
            viewModel.movieAction.emit(MovieAction.FetchMovie)
        }
        return true
    }

    /**
     * ref https://github.com/android/architecture-components-samples/blob/main/ViewBindingSample/app/src/main/java/com/android/example/viewbindingsample/InflateFragment.kt
     */
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}