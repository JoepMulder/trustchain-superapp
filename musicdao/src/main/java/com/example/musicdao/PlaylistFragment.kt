package com.example.musicdao

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.frostwire.jlibtorrent.TorrentInfo
import nl.tudelft.trustchain.common.ui.BaseFragment
import java.lang.Error
import java.lang.Exception

class PlaylistFragment : BaseFragment(R.layout.fragment_playlist) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val localArgs = arguments
        if (localArgs is Bundle) {
            val magnet = localArgs.getString("magnet", "Magnet not found")
            val artists = localArgs.getString("artists", "Artists not found")
            val title = localArgs.getString("title", "Title not found")
            val date = localArgs.getString("date", "Date not found")
            val publisher = localArgs.getString("publisher", "Publisher not found")
            val torrentInfoName = localArgs.getString("torrentInfoName")

            if (magnet != null) {
                val transaction = childFragmentManager.beginTransaction()
                val release = Release(
                    magnet, artists, title, date, publisher, torrentInfoName
                )
                transaction.add(R.id.trackListLinearLayout, release, "release")
                transaction.commit()
            }
        }
    }
}
