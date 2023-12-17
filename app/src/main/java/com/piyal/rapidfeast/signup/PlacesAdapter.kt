package com.piyal.rapidfeast.signup

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.piyal.rapidfeast.R
import com.piyal.rapidfeast.data.model.PlaceModel
import com.squareup.picasso.Picasso


class PlacesAdapter(
    private val context: Context,
    private val places: List<PlaceModel>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<PlacesAdapter.PlaceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_campus, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        holder.bind(places[position], holder.adapterPosition, listener)
    }

    override fun getItemCount(): Int {
        return places.size
    }

    class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageCampus: ImageView = itemView.findViewById(R.id.image_campus)
        private val textCampusName: TextView = itemView.findViewById(R.id.text_campus_name)
        private val textCampusAddress: TextView = itemView.findViewById(R.id.text_campus_address)
        private val layoutRoot: ViewGroup = itemView.findViewById(R.id.layout_root)

        fun bind(place: PlaceModel, position: Int, listener: OnItemClickListener) {
            Picasso.get().load(place.iconUrl).into(imageCampus)
            textCampusName.text = place.name
            textCampusAddress.text = place.address
            layoutRoot.setOnClickListener { listener.onItemClick(place, position) }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(item: PlaceModel, position: Int)
    }
}
