package nl.tudelft.trustchain.currencyii.leaderElection

import io.mockk.every
import io.mockk.just
import nl.tudelft.ipv8.Peer
import nl.tudelft.trustchain.currencyii.CoinCommunity
import org.junit.jupiter.api.Test
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.runs
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import nl.tudelft.ipv8.IPv4Address
import nl.tudelft.ipv8.keyvault.Key
import nl.tudelft.ipv8.keyvault.PrivateKey
import nl.tudelft.ipv8.keyvault.PublicKey
import nl.tudelft.ipv8.messaging.EndpointAggregator
import nl.tudelft.ipv8.messaging.Packet
import nl.tudelft.ipv8.messaging.serializeUShort
import nl.tudelft.ipv8.peerdiscovery.Network
import nl.tudelft.trustchain.common.MarketCommunity
import nl.tudelft.trustchain.currencyii.payload.AlivePayload
import nl.tudelft.trustchain.currencyii.payload.ElectedPayload
import org.junit.jupiter.api.Assertions.*

class payloadTest {
    @Test
    fun alivePayloadTest() {

        val coinCommunity = CoinCommunity()
        val DAOid = "Dao_id"
        val me = mockk<Peer>()

        val me_key = mockk<PublicKey>()

        coinCommunity.myPeer = me

        every { me_key.keyToBin() } returns "me_key".toByteArray()

        every { me.publicKey } returns me_key
        every { me.lamportTimestamp } returns 0u

        every { me.updateClock(any<ULong>() ) } returns Unit
        every { me.key } returns mockk<Key>()

        val daoIdBytes = DAOid.toByteArray()
        val packet = coinCommunity.createAliveResponse(daoIdBytes)
        val packetLastElements = packet.takeLast(daoIdBytes.size)

        assertEquals(daoIdBytes.toList(), packetLastElements)
    }

    @Test
    fun electionPayloadTest() {

        val coinCommunity = CoinCommunity()
        val DAOid = "Dao_id"
        val me = mockk<Peer>()

        val me_key = mockk<PublicKey>()

        coinCommunity.myPeer = me

        every { me_key.keyToBin() } returns "me_key".toByteArray()

        every { me.publicKey } returns me_key
        every { me.lamportTimestamp } returns 0u

        every { me.updateClock(any<ULong>() ) } returns Unit
        every { me.key } returns mockk<Key>()

        val daoIdBytes = DAOid.toByteArray()
        val packet = coinCommunity.createElectedResponse(daoIdBytes)
        val packetLastElements = packet.takeLast(daoIdBytes.size)

        assertEquals(daoIdBytes.toList(), packetLastElements)
    }
    @Test
    fun electionPayloadRequestTest() {

        val coinCommunity = CoinCommunity()
        val DAOid = "Dao_id"
        val me = mockk<Peer>()

        val me_key = mockk<PublicKey>()

        coinCommunity.myPeer = me
        every { me_key.keyToBin() } returns "me_key".toByteArray()

        every { me.publicKey } returns me_key
        every { me.lamportTimestamp } returns 0u

        every { me.updateClock(any<ULong>()) } returns Unit
        every { me.key } returns mockk<Key>()

        val daoIdBytes = DAOid.toByteArray()
        val packet = coinCommunity.createElectionRequest(daoIdBytes)
        val packetLastElements = packet.takeLast(daoIdBytes.size)

        assertEquals(daoIdBytes.toList(), packetLastElements)
    }
}
class LeaderElectionTest {
    private val peersSize = 5
    private var coinCommunity = spyk(CoinCommunity(), recordPrivateCalls = true)
    private val me = mockk<Peer>()
    private val endpoint = mockk<EndpointAggregator>()
    private val network = mockk<Network>(relaxed = true)
    @Test
    fun testOnAliveResponsePacket() {
        val DAOid = "Dao_id"
        val me_key = mockk<PublicKey>()

        every { coinCommunity.myPeer } returns me
        every { coinCommunity.endpoint } returns endpoint
        every { coinCommunity.network } returns network
        every { endpoint.send(any<Peer>(), any()) } just runs

        every { me_key.keyToBin() } returns "me_key".toByteArray()

        every { me.publicKey } returns me_key
        every { me.lamportTimestamp } returns 0u

        every { me.updateClock(any<ULong>()) } returns Unit
        every { me.key } returns mockk<Key>()

        coinCommunity.sendPayload(me, coinCommunity.createAliveResponse(DAOid.toByteArray()))
        verify() { endpoint.send(any<Peer>(), any()) }
        verify() { coinCommunity.onAliveResponsePacket(any<Packet>()) }
        verify() { coinCommunity.getCandidates()[Any()]?.add(any<Peer>()) }
    }
}
