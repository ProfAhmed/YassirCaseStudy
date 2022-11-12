package com.osama.pro.app.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingSource
import app.cash.turbine.test
import com.osama.pro.app.utils.DummyDataGenerator
import com.osama.pro.app.utils.TestCoroutineRule
import com.osama.pro.core.data.Constants
import com.osama.pro.core.data.Constants.DEFAULT_PAGE_SIZE
import com.osama.pro.core.data.RepositoryImpl
import com.osama.pro.core.data.mapper.mapToDomain
import com.osama.pro.core.data.mapper.mapToEntity
import com.osama.pro.core.data.source.local.LocalDataSource
import com.osama.pro.core.data.source.local.dao.MovieDao
import com.osama.pro.core.data.source.local.entity.MovieEntity
import com.osama.pro.core.data.source.remote.RemoteDataSource
import com.osama.pro.core.data.source.remote.network.MovieServices
import com.osama.pro.core.data.states.Resource
import com.osama.pro.core.data.states.Resource.*
import com.osama.pro.core.domain.model.Movie
import com.osama.pro.core.presentation.paging.MoviePagingSource
import io.kotest.assertions.asClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
class RepositoryImplTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val movieServices = mockk<MovieServices>()
    private val movieDao = mockk<MovieDao>()

    /**
     *  @SUTs
     */
    private lateinit var repositoryImpl: RepositoryImpl
    private val remoteDataSource = RemoteDataSource(movieServices)
    private val localDataSource = LocalDataSource(movieDao)

    @Before
    fun setup() {
        repositoryImpl = RepositoryImpl(
            remoteDataSource = remoteDataSource,
            localDataSource = localDataSource,
            dispatcher = testCoroutineRule.testDispatcher
        )
    }

    /**
     *  For fetching PagingData test replaced by testing PagingSource test
     *  source:
     *  https://developer.android.com/topic/libraries/architecture/paging/test
     */

    @Test
    fun `movie paging source should return page when successful load data`() =
        testCoroutineRule.runBlockingTest {
            // Given
            val dummyResponse = DummyDataGenerator.generateListResponseMovie()
            coEvery { movieServices.getMovies() } returns dummyResponse
            val pagingSource = MoviePagingSource(remoteDataSource)

            // When
            val actual = pagingSource.load(
                PagingSource.LoadParams.Refresh(
                    key = null,
                    loadSize = DEFAULT_PAGE_SIZE,
                    placeholdersEnabled = false
                )
            )

            val expected = PagingSource.LoadResult.Page(
                data = DummyDataGenerator.generateMovies(),
                prevKey = null,
                nextKey = null
            )

            // Then
            actual.asClue {
                it shouldNotBe null
                it shouldBe expected
            }
            coVerify { movieServices.getMovies() }
        }

    @Test
    fun `movie paging source should return error`() = testCoroutineRule.runBlockingTest {
        // Given
        val exception = Exception()
        coEvery { movieServices.getMovies() } throws exception
        val pagingSource = MoviePagingSource(remoteDataSource)

        // When
        val actual = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = DEFAULT_PAGE_SIZE,
                placeholdersEnabled = false
            )
        )
        val expected = PagingSource.LoadResult.Error<Int, Movie>(exception)

        // Then
        actual shouldNotBe null
        actual shouldBe expected
        coVerify { movieServices.getMovies() }
    }

    @Test
    fun `should return movie list from db`() = testCoroutineRule.runBlockingTest {
        // Given
        val expected = DummyDataGenerator.generateMovies()
        coEvery { movieDao.getFavoriteMovies() } returns flowOf(expected.map { it.mapToEntity() })

        // When
        repositoryImpl.getFavoriteMovies().test {
            awaitItem().asClue {
                it shouldNotBe null
                it shouldBe expected
            }
            awaitComplete()
        }

        coVerify { movieDao.getFavoriteMovies() }
    }

    @Test
    fun `should fetch movie from network`() = testCoroutineRule.runBlockingTest {
        // Given
        val movieId = 615457
        val expected = DummyDataGenerator.getMovieResponse(movieId)
        coEvery { movieDao.getFavoriteMovie(movieId) } returns flow<MovieEntity?> { emit(null) }
        coEvery {
            movieServices.getMovie(movieId, Constants.appendToResponse)
        } returns expected
        var asserted = false

        // When
        repositoryImpl.fetchMovie(movieId).test {
            // Then
            // Expect first emitted item should be a loading resource
            awaitItem() shouldBe Loading<Resource<Movie>>()

            // Expecting a result in the next item
            when (val resource = awaitItem()) {
                is Error -> throw Error("Resource Error")
                is Loading -> {
                }
                is Success -> {
                    resource.data.asClue {
                        it shouldNotBe null
                        it shouldBe expected.mapToDomain()
                    }
                    asserted = true
                }
            }
            // after item emitted, flow should be completed by this time
            awaitComplete()
        }

        coVerifyOrder {
            movieDao.getFavoriteMovie(movieId)
            movieServices.getMovie(movieId, Constants.appendToResponse)
        }
        println("should fetch movie from network all asserted: $asserted")
    }

    @Test
    fun `should fetch movie from db`() = testCoroutineRule.runBlockingTest {
        // Given
        val movieId = 615457
        val expected = DummyDataGenerator.getMovieEntity(movieId)
        coEvery { movieDao.getFavoriteMovie(movieId) } returns flowOf(expected)
        var asserted = false

        // When
        repositoryImpl.fetchMovie(movieId).test {
            // Then
            // Expect first emitted item should be a loading resource
            awaitItem() shouldBe Loading<Resource<Movie>>()

            // Expecting a result in the next item
            when (val resource: Resource<Movie> = awaitItem()) {
                is Error -> throw Error("Resource Error")
                is Loading -> {
                }
                is Success -> {
                    resource.data.asClue {
                        it shouldNotBe null
                        it shouldBe expected.mapToDomain()
                    }
                    asserted = true
                }
            }
            // after item emitted, flow should be completed by this time
            awaitComplete()
        }

        coVerify { movieDao.getFavoriteMovie(movieId) }
        println("should fetch movie from db all asserted: $asserted")
    }


    @Test
    fun `set movie favorite state to true and false`() = testCoroutineRule.runBlockingTest {
        // Given
        val movieId = 615457
        val data = DummyDataGenerator.getMovieEntity(movieId)
        coEvery { movieDao.addToFavorite(data) } returns 1
        coEvery { movieDao.removeFromFavorite(data) } returns 1

        // When
        with(repositoryImpl) {
            val domain = data.mapToDomain()
            setFavorite(domain, true)
            setFavorite(domain, false)
        }

        // Then
        coVerifyOrder {
            movieDao.addToFavorite(data)
            movieDao.removeFromFavorite(data)
        }
    }

    @After
    fun tearDown() {
        unmockkAll()
    }
}