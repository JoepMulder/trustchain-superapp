package nl.tudelft.trustchain.currencyii.ui.bitcoin


import android.os.Bundle
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import nl.tudelft.ipv8.android.IPv8Android
import nl.tudelft.trustchain.currencyii.CoinCommunity
import nl.tudelft.trustchain.currencyii.R
import nl.tudelft.trustchain.currencyii.databinding.FragmentDebugDashboardBinding
import nl.tudelft.trustchain.currencyii.ui.BaseFragment
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.Collections
import java.util.Locale


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
    private var ipv8 = IPv8Android.getInstance()




    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initListeners()
        initDebugDashboardView()
    }

    private fun initListeners() {
        binding.connectedPeersRefreshSwiper.setOnRefreshListener {
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
            binding.connectedPeersRefreshSwiper.isRefreshing = true
        } catch (_: Exception) {
        }
    }

    private fun disableRefresher() {
        try {
            binding.connectedPeersRefreshSwiper.isRefreshing = false
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
        val peers = ipv8.getOverlay<CoinCommunity>()!!.getPeers();


        adapter = PeerListAdapter(
            this@DebugDashboardFragment,
            peers)

        binding.listView.adapter = adapter;
        // ipv8.myPeer.address is empty so have to get IP with method below
        binding.myIpv4.text = getMyIPv4Address();
        binding.myPublicKey.text = ipv8.myPeer.publicKey.toString()


        disableRefresher()
    }

    companion object {
        @JvmStatic
        fun newInstance() = DebugDashboardFragment()
    }

    fun getMyIPv4Address(): String? {
        try {
            val interfaces: List<NetworkInterface> =
                Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                val addrs: List<InetAddress> = Collections.list(intf.inetAddresses)
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress) {
                        val sAddr = addr.hostAddress
                        val isIPv4 = sAddr.indexOf(':') < 0
                        if (isIPv4) {
                            val x = sAddr
                            val ip = Formatter.formatIpAddress(sAddr.hashCode());
                            return ip
                        }
                    }
                }
            }
        } catch (ignored: java.lang.Exception) {
            return "No IP found"
        }
        return ""
    }
}
