package com.example.gymsystemmanagement.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gymsystemmanagement.R
import com.example.gymsystemmanagement.entity.Usuario
import com.google.android.material.imageview.ShapeableImageView

class MiembrosAdapter(private val miembros: List<Usuario>) :
    RecyclerView.Adapter<MiembrosAdapter.ViewHolder>() {

    // Clase interna que representa cada "tarjeta" o ítem de miembro
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgAvatar: ShapeableImageView = itemView.findViewById(R.id.imgAvatar)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
    }

    // Infla el layout del ítem (item_member.xml)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_members, parent, false)
        return ViewHolder(view)
    }

    // Asigna los datos a cada vista
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val miembro = miembros[position]
        holder.tvName.text = miembro.nombres.split(" ").first() // solo primer nombre
        holder.imgAvatar.setImageResource(R.drawable.ic_users) // ícono temporal
    }

    // Devuelve cuántos ítems hay en la lista
    override fun getItemCount(): Int = miembros.size
}
