package com.example.gymsystemmanagement.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gymsystemmanagement.R
import com.example.gymsystemmanagement.entity.Usuario
import com.google.android.material.card.MaterialCardView

class HistorialAdapter(
    private val listaUsuarios: List<Usuario>,
    private val onOpciones: (Usuario) -> Unit
) : RecyclerView.Adapter<HistorialAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: MaterialCardView = view.findViewById(R.id.mainCard)
        val imgUsuario: ImageView = view.findViewById(R.id.imgUsuario)
        val ivMenu: ImageView = view.findViewById(R.id.ivMenu)
        val tvCodigo: TextView = view.findViewById(R.id.tvCodigo)
        val tvNombreCompleto: TextView = view.findViewById(R.id.tvNombreCompleto)
        val tvCelular: TextView = view.findViewById(R.id.tvCelular)
        val tvFechaRegistro: TextView = view.findViewById(R.id.tvFechaRegistro)
        val tvRol: TextView = view.findViewById(R.id.tvRol)
        val tvEstado: TextView = view.findViewById(R.id.tvEstado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historial, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val usuario = listaUsuarios[position]
        holder.tvCodigo.text = "Código: ${usuario.id}"
        holder.tvNombreCompleto.text = "${usuario.nombres} ${usuario.apellidoPaterno} ${usuario.apellidoMaterno}"
        holder.tvCelular.text = "Celular: ${usuario.celular}"
        holder.tvFechaRegistro.text = "Fecha: ${usuario.fechaRegistro}"
        holder.tvRol.text = "Rol: ${usuario.rol}"
        holder.tvEstado.text = "Estado: ${usuario.estado}"

        holder.ivMenu.setOnClickListener {
            onOpciones(usuario)
        }
    }

    override fun getItemCount(): Int = listaUsuarios.size
}
