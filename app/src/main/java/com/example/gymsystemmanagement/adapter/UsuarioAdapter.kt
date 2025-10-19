package com.example.gymsystemmanagement.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gymsystemmanagement.R
import com.example.gymsystemmanagement.entity.Usuario
import com.google.android.material.imageview.ShapeableImageView

class UsuarioAdapter(private val usuario: List<Usuario>) : RecyclerView.Adapter<UsuarioAdapter.ViewHolder>() {

        private var onItemClickListener: ((Usuario) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_usuarios, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val usuario=usuario[position]
        holder.tvName.text=usuario.nombres
        holder.imgAvatar.setImageResource(R.drawable.ic_users)
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(usuario)
        }
    }

    override fun getItemCount(): Int { return usuario.size }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgAvatar: ShapeableImageView = itemView.findViewById(R.id.imgAvatar)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
    }

}
