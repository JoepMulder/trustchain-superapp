package nl.tudelft.trustchain.currencyii.util

import nl.tudelft.ipv8.IPv4Address
import nl.tudelft.ipv8.Peer
import nl.tudelft.trustchain.currencyii.payload.ElectionPayload
import nl.tudelft.trustchain.currencyii.CoinCommunity
import nl.tudelft.trustchain.currencyii.payload.AlivePayload
import nl.tudelft.trustchain.currencyii.payload.ElectedPayload

class LeaderElectionHelper {

    private val coinCommunity = CoinCommunity()
    private var responses = ArrayList<Peer>()
    private var leader: Peer? = null
    fun onElectionRequest(peer: Peer, payload: ElectionPayload, peers: List<Peer>, address: IPv4Address) {
        val aliveResponse = coinCommunity.createAliveResponse(payload.toString())
        coinCommunity.sendPayload(peer, aliveResponse)

        responses = ArrayList()
        leader = null

        val higherPeers = ArrayList<Peer>()
        for (p in peers) {
            if (p.address.hashCode() > address.hashCode()) {
                higherPeers.add(p)
            }
        }

        if(higherPeers.isEmpty()) {
            val electedPayload = coinCommunity.createElectedResponse(payload.toString())
            coinCommunity.sendPayload(peer, electedPayload)
            leader = coinCommunity.myPeer
            return
        }
        var lastTime = System.currentTimeMillis()
        var i = 0
        for (p in higherPeers) {
            // Send election request to the peer with the highest hash
            val generateedPayload = coinCommunity.createElectionRequest(payload.toString())
            i++
            coinCommunity.sendPayload(p, generateedPayload)
            if(i == higherPeers.size) {
                lastTime = System.currentTimeMillis()
            }
        }
        while (System.currentTimeMillis() - lastTime < 1000) {
            // Wait for responses
        }
        if(responses.isNotEmpty()){
            val electedPayload = coinCommunity.createElectedResponse(payload.toString())
            coinCommunity.sendPayload(peer, electedPayload)
            leader = coinCommunity.myPeer
        }

    }
    fun onAliveResponse(peer:Peer, payload: AlivePayload) {
        responses.add(peer)
    }

    fun onElectedResponse(peer:Peer, payload: ElectedPayload) {
        leader = peer
    }

}
