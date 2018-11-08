package com.pickledgames.stardewvalleyguide.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pickledgames.stardewvalleyguide.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.list_item_museum_item_location.*

class MuseumItemLocationsAdapter(
        private val locations: List<String>
) : RecyclerView.Adapter<MuseumItemLocationsAdapter.MuseumItemLocationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MuseumItemLocationViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_museum_item_location, parent, false)
        return MuseumItemLocationViewHolder(v)
    }

    override fun getItemCount(): Int {
        return locations.size
    }

    override fun onBindViewHolder(holder: MuseumItemLocationViewHolder, position: Int) {
        holder.bindLocation(locations[position])
    }

    class MuseumItemLocationViewHolder(
            override val containerView: View
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bindLocation(location: String) {
            museum_item_location_text_view?.text = location
        }
    }
}
