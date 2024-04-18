package nl.tudelft.trustchain.currencyii.leaderElection

import io.mockk.every
import nl.tudelft.ipv8.Peer
import nl.tudelft.trustchain.currencyii.CoinCommunity
import org.junit.jupiter.api.Test
import io.mockk.mockk
import nl.tudelft.ipv8.IPv4Address
import nl.tudelft.ipv8.keyvault.Key
import nl.tudelft.ipv8.keyvault.PublicKey
import nl.tudelft.ipv8.messaging.serializeUShort
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
