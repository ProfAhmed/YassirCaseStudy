package com.osama.pro.app.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import com.osama.pro.app.utils.TestCoroutineRule
import com.osama.pro.core.domain.interactor.DetailUseCase
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
class DetailViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @RelaxedMockK
    private lateinit var savedStateHandle: SavedStateHandle

    @RelaxedMockK
    private lateinit var detailUseCase: DetailUseCase

    @MockK
    private lateinit var favoriteObserver: Observer<Boolean>

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        every { savedStateHandle.get<Int>(DetailViewModel.EXTRA_ID) } returns 0
        every { favoriteObserver.onChanged(any()) } returns Unit
    }

    @After
    fun tearDown() = unmockkAll()
}