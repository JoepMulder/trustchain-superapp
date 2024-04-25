package nl.tudelft.trustchain.currencyii.leaderElection

import io.mockk.every
import nl.tudelft.ipv8.Peer
import nl.tudelft.trustchain.currencyii.CoinCommunity
import org.junit.jupiter.api.Test
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import nl.tudelft.ipv8.keyvault.Key
import nl.tudelft.ipv8.keyvault.LibNaClSK
import nl.tudelft.ipv8.keyvault.PublicKey
import nl.tudelft.ipv8.messaging.EndpointAggregator
import nl.tudelft.ipv8.messaging.Packet
import nl.tudelft.ipv8.peerdiscovery.Network
import nl.tudelft.ipv8.util.hexToBytes
import org.junit.jupiter.api.Assertions.*

import com.goterl.lazysodium.LazySodiumJava
import com.goterl.lazysodium.SodiumJava

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
    private lateinit var lazySodium: LazySodiumJava
    private lateinit var key: LibNaClSK

    private lateinit var myPeer: Peer

    private lateinit var community: CoinCommunity
    private lateinit var network: Network
    private lateinit var endpoint: EndpointAggregator
    private lateinit var handler: (Packet) -> Unit

    fun init() {

        val lazySodium = LazySodiumJava(SodiumJava())
        val key = LibNaClSK(
            "81df0af4c88f274d5228abb894a68906f9e04c902a09c68b9278bf2c7597eaf6".hexToBytes(),
            "c5c416509d7d262bddfcef421fc5135e0d2bdeb3cb36ae5d0b50321d766f19f2".hexToBytes(),
            lazySodium
        )

        val myPeer = Peer(key)

        val community = CoinCommunity()
        val network = Network()
        val endpoint = spyk(EndpointAggregator(mockk(relaxed = true), null))
        val handler = mockk<(Packet) -> Unit>(relaxed = true)

        this.lazySodium = lazySodium
        this.key = key
        this.myPeer = myPeer
        this.network = network
        this.endpoint = endpoint
        this.handler = handler
        this.community = community

        community.myPeer = myPeer
        community.endpoint = endpoint
        community.network = network
        community.evaProtocolEnabled = true

    }
    @Test
    fun onAlivePacketTest() {
        init()
        community.messageHandlers[CoinCommunity.MessageId.ALIVE_RESPONSE] = handler
        community.createAliveResponse(
            "x".repeat(64).toByteArray()
        ).let { packet ->
            community.onPacket(Packet(myPeer.address, packet))
        }
        verify { handler(any()) }
    }
    @Test
    fun handleAlivePacketTest() {

        init()
        val spykedCommunity = spyk(community)
        spykedCommunity.createAliveResponse(
            "x".repeat(64).toByteArray()
        ).let { packet ->
            println(packet.size); spykedCommunity.onAliveResponsePacket(Packet(myPeer.address, packet))
        }
        verify { spykedCommunity.onAliveResponse(any(), any()) }
        verify { spykedCommunity.getCandidates()}
    }
    @Test
    fun onElectedPacketTest() {
        init()
        community.messageHandlers[CoinCommunity.MessageId.ELECTED_RESPONSE] = handler
        community.createElectedResponse(
            "x".repeat(64).toByteArray()
        ).let { packet ->
            community.onPacket(Packet(myPeer.address, packet))
        }
        verify { handler(any()) }
    }
    @Test
    fun handleElectedPacketTest() {
        init()
        val spykedCommunity = spyk(community)
        spykedCommunity.createElectedResponse(
            "x".repeat(64).toByteArray()
        ).let { packet ->
            println(packet.size); spykedCommunity.onElectedResponsePacket(Packet(myPeer.address, packet))
        }
        verify { spykedCommunity.onElectedResponse(any(), any()) }
    }
    @Test
    fun onElectionPacketTest() {
        init()
        community.messageHandlers[CoinCommunity.MessageId.ELECTION_REQUEST] = handler
        community.createElectionRequest(
            "x".repeat(64).toByteArray()
        ).let { packet ->
            community.onPacket(Packet(myPeer.address, packet))
        }
        verify { handler(any()) }
    }
    @Test
    fun handleElectionPacketTest() {
        init()
        val spykedCommunity = spyk(community)
        spykedCommunity.createElectedResponse(
            "x".repeat(64).toByteArray()
        ).let { packet ->
            println(packet.size); spykedCommunity.onElectionRequestPacket(Packet(myPeer.address, packet))
        }
        verify { spykedCommunity.onElectionRequest(any(), any()) }
        verify { spykedCommunity.createAliveResponse(any()) }
        verify { spykedCommunity.sendPayload(any(), any()) }
        verify { spykedCommunity.getCandidates() }

    }
}
