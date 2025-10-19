package com.example.gymsystemmanagement.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gymsystemmanagement.R
import com.example.gymsystemmanagement.entity.Usuario
import com.google.android.material.imageview.ShapeableImageView

class HistorialAdapter(private val listaHistorial: List<Usuario>) : RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorialViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_historial, parent, false)
        return HistorialViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistorialViewHolder, position: Int) {
        val Usuario = listaHistorial[position]
        holder.tvCodigo.text=Usuario.id.toString()
        holder.tvNomCompleto.text = "${Usuario.nombres} ${Usuario.apellidoMaterno}"
        holder.tvCelular.text= Usuario.celular.toString()
        holder.tvFechaRegistro.text= "${"Fecha Registro: "}${Usuario.fechaRegistro.toString()}"
        holder.tvRol.text=Usuario.rol
        holder.tvEstado.text=Usuario.estado
    }

    override fun getItemCount(): Int {
        return listaHistorial.size
    }

    inner class HistorialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var tvCodigo : TextView=itemView.findViewById(R.id.tvCodigo)
        var tvNomCompleto : TextView=itemView.findViewById(R.id.tvNombreCompleto)
        var tvCelular : TextView=itemView.findViewById(R.id.tvCelular)
        var tvFechaRegistro : TextView=itemView.findViewById(R.id.tvFechaRegistro)
        var tvRol : TextView=itemView.findViewById(R.id.tvRol)
        var tvEstado : TextView=itemView.findViewById(R.id.tvEstado)

    }
}