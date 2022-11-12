package com.osama.pro.app.home.movie

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.osama.pro.app.state.UiState
import com.osama.pro.app.utils.DummyDataGenerator
import com.osama.pro.app.utils.TestCoroutineRule
import com.osama.pro.app.utils.collectDataForTest
import com.osama.pro.core.domain.interactor.MovieUseCase
import io.kotest.assertions.asClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
class MovieViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @RelaxedMockK
    private lateinit var movieUseCase: MovieUseCase
    private lateinit var viewModel: MovieViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
        viewModel = MovieViewModel(movieUseCase)
    }

    @Test
    fun `get MovieStream from UseCase`() = testCoroutineRule.runBlockingTest {
        // Given
        val pagingData = DummyDataGenerator.getMoviePagingData()
        // mock return data from the movieUseCase
        every { movieUseCase.fetchMovies() } returns pagingData
        val expected = DummyDataGenerator.generateMovies()
        var itemIsAsserted = false

        // When
        viewModel.state.test {
            when(val state = awaitItem()){
                is UiState.Error -> {}
                UiState.Idle -> {}
                UiState.Loading -> {}
                is UiState.Success -> {
                    //Then
                    state.data.collectDataForTest(testCoroutineRule.testDispatcher).asClue {
                        it shouldNotBe null
                        it shouldBe expected
                    }
                    itemIsAsserted = true
                    awaitComplete()
                }
            }
        }

        verify { movieUseCase.fetchMovies() }
        println("All Asserted : $itemIsAsserted")
    }
//
//    @Test
//    fun `get MovieStream from UseCase but empty`() = testCoroutineRule.runBlockingTest {
//        // Given
//        // Create a dummy flow that emits PagingData from the generated data
//        val dummyFlowPagingData: Flow<PagingData<Movie>> = flow { emit(PagingData.empty()) }
//        // mock return data from the movieUseCase
//        every { movieUseCase.fetchMovies() } returns dummyFlowPagingData
//        val expected = arrayListOf<Movie>()
//
//        var itemIsAsserted = false
//        viewModel.movieAction.send(MovieAction.FetchMovie)
//        viewModel.state.test {
//            when(val state = awaitItem()){
//                is UiState.Error -> {}
//                UiState.Idle -> {}
//                UiState.Loading -> {}
//                is UiState.Success -> {
//                    // Then
//                    state.data.collectDataForTest(testCoroutineRule.testDispatcher).asClue{
//                        it shouldNotBe null
//                        it shouldBe expected
//                    }
//                    itemIsAsserted = true
//                    awaitComplete()
//                }
//            }
//        }
//
//        verify { movieUseCase.fetchMovies() }
//        println("All Asserted : $itemIsAsserted")
//    }

    @After
    fun tearDown() {
        unmockkAll()
    }
}


