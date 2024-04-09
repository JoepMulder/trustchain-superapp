package nl.tudelft.trustchain.currencyii.util

import android.util.Log
import nl.tudelft.ipv8.IPv4Address
import nl.tudelft.ipv8.Peer
import nl.tudelft.trustchain.currencyii.payload.ElectionPayload
import nl.tudelft.trustchain.currencyii.CoinCommunity
import nl.tudelft.trustchain.currencyii.coin.WalletManagerAndroid
import nl.tudelft.trustchain.currencyii.payload.AlivePayload
import nl.tudelft.trustchain.currencyii.payload.ElectedPayload
import nl.tudelft.trustchain.currencyii.payload.SignPayload


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
            val electedPayload = coinCommunity.createElectedResponse(payload.toString(), coinCommunity.myPeer)
            coinCommunity.sendPayload(peer, electedPayload)
            leader = coinCommunity.myPeer
            return
        }
        var lastTime = System.currentTimeMillis()
        var i = 0
        for (p in higherPeers) {
            // Send election request to the peer with the highest hash
            val generatedPayload = coinCommunity.createElectionRequest(payload.toString())
            i++
            coinCommunity.sendPayload(p, generatedPayload)
            if(i == higherPeers.size) {
                lastTime = System.currentTimeMillis()
            }
        }
        while (System.currentTimeMillis() - lastTime < 1000) {
            // Wait for responses
        }
        if(responses.isNotEmpty()){
            leader = coinCommunity.myPeer
            val electedPayload = coinCommunity.createElectedResponse(payload.toString(), leader!!)
            coinCommunity.sendPayload(peer, electedPayload)
        }

    }
    fun onAliveResponse(peer:Peer, payload: AlivePayload) {
        responses.add(peer)
    }

    fun onElectedResponse(peer:Peer, payload: ElectedPayload) {

        val pair = ElectedPayload.deserializeBytes(payload.serialize(), 0)
        Log.d("LEADER", "Elected: " + pair.second.toString())
        leader = pair.second
    //        if (!this.checkLeaderExists()) {
//            leader = pair.second
//        }
//        else if (!checkIsLeader(pair.second)) {
//            coinCommunity.sendPayload(leader!!, AlivePayload(AlivePayload.deserializeBytes(payload.serialize(), 0).toString().toByteArray()).serialize())
//        }


    }
    fun checkIsLeader(peer:Peer): Boolean {
        return if (this.checkLeaderExists()) {
            false
        } else {
            leader!!.address.equals(peer.address)
        }
    }
    fun checkLeaderExists(): Boolean {
        return leader != null
    }
    fun sendProposalToLeader(payload: SignPayload) {
        this.leader?.let { coinCommunity.sendPayload(this.leader!!, payload.serialize()) }
    }

    fun onLeaderSignProposal(payload: SignPayload) {
        // Current peer is the leader and will sign the proposal
        // Create a new shared wallet using the signatures of the others.
        // Broadcast the new shared bitcoin wallet on trust chain.
//        try {
//            this.joinBitcoinWallet(
//                mostRecentSWBlock.transaction,
//                proposeBlockData,
//                signatures,
//                context
//            )
//            // Add new nonceKey after joining a DAO
//            WalletManagerAndroid.getInstance()
//                .addNewNonceKey(proposeBlockData.SW_UNIQUE_ID, context)
//        } catch (t: Throwable) {
////                Log.e("Coin", "Joining failed. ${t.message ?: "No further information"}.")
//        }
    }

}

