package com.mrjahid.smartdevice

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.io.IOException

internal class CustomAdapter(private var itemsList: List<Word>) :

    RecyclerView.Adapter<CustomAdapter.MyViewHolder>() {

    private lateinit var mListener: onItemClickListener

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }


    internal inner class MyViewHolder(view: View, listener: onItemClickListener) :
        RecyclerView.ViewHolder(view) {
        var title: TextView = view.findViewById(R.id.title)
        var img: ImageView = view.findViewById(R.id.picture)


        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }

    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item, parent, false)
        return MyViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item:Word = itemsList[position]
        holder.title.text = item.title
        Picasso.get().load(item.img).into(holder.img);
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }


}