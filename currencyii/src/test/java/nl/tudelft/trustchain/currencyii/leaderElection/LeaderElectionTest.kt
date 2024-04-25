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
import io.mockk.just
import io.mockk.runs
import nl.tudelft.ipv8.IPv4Address
import nl.tudelft.trustchain.currencyii.payload.ElectionPayload
import org.junit.Before
import org.junit.jupiter.api.BeforeAll

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

class onElectionpayloadTest() {
    companion object {
        lateinit var community: CoinCommunity
        lateinit var candidates: HashMap<ByteArray, ArrayList<Peer>>
        lateinit var currentLeader: HashMap<ByteArray, Peer?>

        lateinit var ipv4P1: IPv4Address
        lateinit var ipv4P2: IPv4Address
        lateinit var ipv4P3: IPv4Address
        lateinit var ipv4P4: IPv4Address


        lateinit var peer1: Peer
        lateinit var peer2: Peer
        lateinit var peer3: Peer
        lateinit var peer4: Peer
        val daoID: ByteArray = "x".repeat(64).toByteArray()


        @BeforeAll
        @JvmStatic
        fun setup() {
            community = mockk<CoinCommunity>(relaxed = true)
            candidates = HashMap()
            currentLeader = HashMap()
            every { community.getCandidates() } returns candidates
            every { community.getCurrentLeader() } returns currentLeader

            ipv4P1 = mockk<IPv4Address>()
            ipv4P2 = mockk<IPv4Address>()
            ipv4P3 = mockk<IPv4Address>()
            ipv4P4 = mockk<IPv4Address>()

            peer1 = mockk<Peer>()
            peer2 = mockk<Peer>()
            peer3 = mockk<Peer>()
            peer4 = mockk<Peer>()

            every { ipv4P1.hashCode() } returns 1
            every { ipv4P2.hashCode() } returns 2
            every { ipv4P3.hashCode() } returns 3
            every { ipv4P4.hashCode() } returns 4

            every { peer1.address } returns ipv4P1
            every { peer2.address } returns ipv4P2
            every { peer3.address } returns ipv4P3
            every { peer4.address } returns ipv4P4

            every {
                community.onElectionRequest(
                    any(),
                    any()
                )
            } answers { callOriginal() }

            every { community.createAliveResponse(any()) } answers { callOriginal() }
            every { community.createElectedResponse(any()) } answers { callOriginal() }
            every { community.sendPayload(any(), any()) } just runs

        }
    }
    @Test
    fun onElectionRequestTestLargest(){
        every { community.myPeer } returns peer4
        every { community.getPeers()} answers { listOf(peer1, peer2, peer3)}
        val alivePacket = community.createAliveResponse(daoID)
        val electedPacket = community.createElectedResponse(daoID)

        community.onElectionRequest( peer3, ElectionPayload(daoID))

        verify { community.sendPayload(peer3, alivePacket) }
        verify { community.sendPayload(peer3, electedPacket) }
        verify { community.getPeers() }

        verify { ipv4P1.hashCode() }
        verify { ipv4P2.hashCode() }
        verify { ipv4P3.hashCode() }

        community.getCurrentLeader()[daoID]?.let { assert (it.equals(peer4)) }
    }

    @Test
    fun onElectionRequestNotLargestNoReplies(){
        every { community.myPeer } returns peer2
        every { community.getPeers()} answers { listOf(peer1, peer3, peer4)}

        val alivePacket = community.createAliveResponse(daoID)
        val electionPacket = community.createElectionRequest(daoID)
        val electedPacket = community.createElectedResponse(daoID)


        community.onElectionRequest( peer1, ElectionPayload(daoID))

        verify { community.sendPayload(peer1, alivePacket) }
        verify { community.sendPayload(peer1, electedPacket) }

        verify { community.sendPayload(peer3,electionPacket) }
        verify { community.sendPayload(peer4,electionPacket) }

        verify { community.getPeers() }

        verify { ipv4P1.hashCode() }
        verify { ipv4P3.hashCode() }
        verify { ipv4P4.hashCode() }

        community.getCurrentLeader()[daoID]?.let { assert (it.equals(peer2)) }
    }

    @Test
    fun onElectionRequestNotLargestOneReply(){
        every { community.myPeer } returns peer2
        every { community.getPeers()} answers { listOf(peer1, peer3, peer4)}
        candidates[daoID] = ArrayList()
        candidates[daoID]?.add(peer3)
        val alivePacket = community.createAliveResponse(daoID)
        val electionPacket = community.createElectionRequest(daoID)
        val electedPacket = community.createElectedResponse(daoID)

        community.onElectionRequest( peer1, ElectionPayload(daoID))

        verify { community.sendPayload(peer1, alivePacket) }
        verify { community.sendPayload(peer1, electedPacket) }

        verify { community.sendPayload(peer3,electionPacket) }
        verify { community.sendPayload(peer4,electionPacket) }

        verify { community.getPeers() }

        verify { ipv4P1.hashCode() }
        verify { ipv4P3.hashCode() }
        verify { ipv4P4.hashCode() }

        assert(community.getCurrentLeader()[daoID] == null)
    }
}
