package nl.tudelft.trustchain.currencyii

import nl.tudelft.ipv8.Peer
import nl.tudelft.ipv8.peerdiscovery.Network

class CoinCommunityTest: BaseCoinCommunityTest() {
    private fun getCommunity(): TestCoinCommunity {
        val myPrivateKey = getPrivateKey()
        val myPeer = Peer(myPrivateKey)
        val endpoint = getEndpoint()
        val network = Network()

        val community = TestCoinCommunity()
        community.myPeer = myPeer
        community.endpoint = endpoint
        community.network = network

        return community
    }


}
