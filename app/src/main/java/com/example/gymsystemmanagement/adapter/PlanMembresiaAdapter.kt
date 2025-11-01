package com.example.gymsystemmanagement.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gymsystemmanagement.entity.PlanMembresia
import com.example.gymsystemmanagement.R

class PlanMembresiaAdapter(private val listaPlanes: List<PlanMembresia>,
    private val onOpciones: (PlanMembresia) -> Unit): RecyclerView.Adapter<PlanMembresiaAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_planes_membresia, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val plan = listaPlanes[position]
            holder.tvNombre.text = plan.nombre
            holder.tvDuracion.text = plan.duracionMeses.toString()
            holder.tvPrecio.text = plan.precio.toString()
            holder.tvDescripcion.text = plan.descripcion

            holder.ivOpciones.setOnClickListener {
                onOpciones(plan)
            }
        }

        override fun getItemCount(): Int {
            return listaPlanes.size
        }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val tvDuracion : TextView = itemView.findViewById(R.id.tvDuracion)
        val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecio)
        val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
        val ivOpciones: ImageView = itemView.findViewById(R.id.ivOpciones)
    }
}