package nl.tudelft.trustchain.currencyii.ui.bitcoin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nl.tudelft.ipv8.android.IPv8Android
import nl.tudelft.ipv8.attestation.trustchain.TrustChainBlock
import nl.tudelft.ipv8.util.toHex
import nl.tudelft.trustchain.currencyii.CoinCommunity
import nl.tudelft.trustchain.currencyii.R
import nl.tudelft.trustchain.currencyii.coin.BitcoinNetworkOptions
import nl.tudelft.trustchain.currencyii.coin.MAIN_NET_WALLET_NAME
import nl.tudelft.trustchain.currencyii.coin.REG_TEST_WALLET_NAME
import nl.tudelft.trustchain.currencyii.coin.TEST_NET_WALLET_NAME
import nl.tudelft.trustchain.currencyii.coin.WalletManagerAndroid
import nl.tudelft.trustchain.currencyii.coin.WalletManagerConfiguration
import nl.tudelft.trustchain.currencyii.databinding.FragmentDebugDashboardBinding
import nl.tudelft.trustchain.currencyii.databinding.FragmentMyDaosBinding
import nl.tudelft.trustchain.currencyii.sharedWallet.SWJoinBlockTransactionData
import nl.tudelft.trustchain.currencyii.ui.BaseFragment
import java.io.File

/**
 * A simple [Fragment] subclass.
 * Use the [DebugDashboardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DebugDashboardFragment : BaseFragment(R.layout.fragment_debug_dashboard) {
    @Suppress("ktlint:standard:property-naming") // False positive
    private var _binding: FragmentDebugDashboardBinding? = null
    private val binding get() = _binding!!
    private var adapter: PeerListAdapter? = null
    private var isFetching: Boolean = false


    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initListeners()
        initDebugDashboardView()
    }

    private fun initListeners() {
        binding.joinDaoRefreshSwiper.setOnRefreshListener {
            this.refresh()
        }
    }

    private fun refresh() {
        enableRefresher()
        lifecycleScope.launchWhenStarted {
            getPeersAndUpdateUI()
        }
    }

    private fun enableRefresher() {
        try {
            this.isFetching = true
            binding.joinDaoRefreshSwiper.isRefreshing = true
        } catch (_: Exception) {
        }
    }

    private fun disableRefresher() {
        try {
            binding.joinDaoRefreshSwiper.isRefreshing = false
        } catch (_: Exception) {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        showNavBar()
        _binding = FragmentDebugDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initDebugDashboardView() {
        getPeersAndUpdateUI()
    }

    private fun getPeersAndUpdateUI() {
        val peers = IPv8Android.getInstance().getOverlay<CoinCommunity>()!!.getPeers();
        adapter = PeerListAdapter(
            this@DebugDashboardFragment,
            peers)

        binding.listView.adapter = adapter

        disableRefresher()
    }






    companion object {
        @JvmStatic
        fun newInstance() = DebugDashboardFragment()
    }
}
