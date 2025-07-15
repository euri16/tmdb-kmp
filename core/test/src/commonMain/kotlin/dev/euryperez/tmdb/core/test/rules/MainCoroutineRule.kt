package dev.euryperez.tmdb.core.test.rules

import dev.euryperez.tmdb.core.test.BaseTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@ExperimentalCoroutinesApi
class MainCoroutineRule(
    private val dispatcher: TestDispatcher = StandardTestDispatcher(),
) : BaseTest {

    override fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    override fun tearDown() {
        Dispatchers.resetMain()
    }
}
