package com.example.lab3.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lab3.R
import com.example.lab3.model.Track

class TracksAdapter(private val items: List<Track>) : RecyclerView.Adapter<TracksAdapter.VH>() {
    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title)
        val artist: TextView = view.findViewById(R.id.artist)
        val play: ImageButton = view.findViewById(R.id.play)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.artist.text = item.artist
    }

    override fun getItemCount() = items.size
}