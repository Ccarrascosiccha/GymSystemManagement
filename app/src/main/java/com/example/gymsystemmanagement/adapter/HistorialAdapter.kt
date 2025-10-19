package com.example.gymsystemmanagement.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gymsystemmanagement.R
import com.example.gymsystemmanagement.entity.Usuario

class HistorialAdapter(private val listaHistorial: List<Usuario>) : RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistorialViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historial, parent, false)
        return HistorialViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: HistorialViewHolder,
        position: Int
    ) {
        val Usuario = listaHistorial[position]
        holder.tvCodigo.text = Usuario.id.toString()
        holder.tvNombre.text = "${Usuario.nombres} ${Usuario.apellidoMaterno}"
        holder.tvCelular.text= Usuario.celular.toString()
        holder.tvSexo.text= Usuario.sexo.toString()
        holder.tvCorreo.text=Usuario.correo
        holder.tvClave.text=Usuario.clave
    }

    override fun getItemCount(): Int {
        return listaHistorial.size
    }

    inner class HistorialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvCodigo : TextView=itemView.findViewById(R.id.tvCodigo)
        var tvNombre : TextView=itemView.findViewById(R.id.tvNombre)
        var tvCelular : TextView=itemView.findViewById(R.id.tvCelular)
        var tvSexo : TextView=itemView.findViewById(R.id.tvSexo)
        var tvCorreo : TextView=itemView.findViewById(R.id.tvCorreo)
        var tvClave : TextView=itemView.findViewById(R.id.tvClave)
    }
}