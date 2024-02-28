package com.pickledgames.stardewvalleyguide.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pickledgames.stardewvalleyguide.databinding.ListItemMuseumItemLocationBinding
import kotlinx.android.extensions.LayoutContainer

class MuseumItemLocationsAdapter(
        private val locations: List<String>
) : RecyclerView.Adapter<MuseumItemLocationsAdapter.MuseumItemLocationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MuseumItemLocationViewHolder {
        val binding = ListItemMuseumItemLocationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MuseumItemLocationViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return locations.size
    }

    override fun onBindViewHolder(holder: MuseumItemLocationViewHolder, position: Int) {
        holder.bindLocation(locations[position])
    }

    class MuseumItemLocationViewHolder(
        private val binding: ListItemMuseumItemLocationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindLocation(location: String) {
            binding.museumItemLocationTextView.text = location
        }
    }
}
