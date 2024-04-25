@file:Suppress("DEPRECATION") // TODO: replace TestCoroutineDispatcher.
package nl.tudelft.trustchain.currencyii


import com.goterl.lazysodium.LazySodiumJava
import com.goterl.lazysodium.SodiumJava
import io.mockk.mockk
import io.mockk.spyk

import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import nl.tudelft.ipv8.keyvault.LibNaClSK
import nl.tudelft.ipv8.keyvault.PrivateKey
import nl.tudelft.ipv8.messaging.EndpointAggregator
import nl.tudelft.ipv8.util.hexToBytes
import org.junit.After
import org.junit.Before
import kotlinx.coroutines.Dispatchers
import nl.tudelft.ipv8.*

private val lazySodium = LazySodiumJava(SodiumJava())


open class BaseCoinCommunityTest {

    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
        testDispatcher.cleanupTestCoroutines()
    }

    protected fun getPrivateKey(): PrivateKey {
        val privateKey = "81df0af4c88f274d5228abb894a68906f9e04c902a09c68b9278bf2c7597eaf6"
        val signSeed = "c5c416509d7d262bddfcef421fc5135e0d2bdeb3cb36ae5d0b50321d766f19f2"
        return LibNaClSK(privateKey.hexToBytes(), signSeed.hexToBytes(), lazySodium)
    }

    protected fun getMyPeer(): Peer {
        return Peer(getPrivateKey())
    }

    protected fun getEndpoint(): EndpointAggregator {
        return spyk(EndpointAggregator(mockk(relaxed = true), null))
    }
}
