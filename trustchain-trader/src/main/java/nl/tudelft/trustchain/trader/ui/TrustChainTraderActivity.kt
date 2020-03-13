package nl.tudelft.trustchain.trader.ui

import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import nl.tudelft.trustchain.common.BaseActivity
import nl.tudelft.trustchain.trader.R

class TrustChainTraderActivity : BaseActivity() {
    override val navigationGraph = R.navigation.nav_graph_trader
    override val bottomNavigationMenu = R.menu.bottom_navigation_menu_trader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appBarConfiguration = AppBarConfiguration.Builder(
            setOf(R.id.transferFragment, R.id.traderFragment, R.id.peerFragment, R.id.AI_HistoryFragment)).build()
        setupActionBarWithNavController(findNavController(R.id.navHostFragment), appBarConfiguration)
    }
}
