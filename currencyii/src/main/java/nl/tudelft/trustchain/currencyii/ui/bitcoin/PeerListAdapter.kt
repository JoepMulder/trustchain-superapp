package nl.tudelft.trustchain.currencyii.ui.bitcoin

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import nl.tudelft.ipv8.Peer
import nl.tudelft.ipv8.attestation.trustchain.TrustChainBlock
import nl.tudelft.ipv8.util.hexToBytes
import nl.tudelft.trustchain.currencyii.CoinCommunity
import nl.tudelft.trustchain.currencyii.R
import nl.tudelft.trustchain.currencyii.coin.WalletManagerAndroid
import nl.tudelft.trustchain.currencyii.databinding.PeerRowDataBinding
import nl.tudelft.trustchain.currencyii.sharedWallet.SWSignatureAskTransactionData
import nl.tudelft.trustchain.currencyii.sharedWallet.SWTransferFundsAskTransactionData
import nl.tudelft.trustchain.currencyii.ui.BaseFragment
import nl.tudelft.trustchain.currencyii.util.taproot.CTransaction
import org.bitcoinj.core.Coin
import java.text.SimpleDateFormat

class PeerListAdapter(
    private val context: BaseFragment,
    private val items: List<Peer>
) : BaseAdapter() {
    override fun getView(
        p0: Int,
        p1: View?,
        p2: ViewGroup?
    ): View {
        val binding =
            if (p1 != null) {
                PeerRowDataBinding.bind(p1)
            } else {
                PeerRowDataBinding.inflate(context.layoutInflater)
            }

        val view = binding.root

        val peer = items[p0]
        val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm")

        val ipv4 = binding.ipv4
        var last_request = binding.lastRequest;
        var last_response = binding.lastResponse;
        var public_key = binding.publicKey



        ipv4.text = peer.address.ip
        last_request.text = peer.lastRequest?.let { formatter.format(it) };
        last_response.text = peer.lastResponse?.let { formatter.format(it) };
        public_key.text = peer.publicKey.toString();


        return view
    }



    override fun getItem(p0: Int): Any {
        return items[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return items.size
    }
}
