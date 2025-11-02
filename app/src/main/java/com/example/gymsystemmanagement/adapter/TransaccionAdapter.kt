package com.example.gymsystemmanagement.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.gymsystemmanagement.R
import com.example.gymsystemmanagement.entity.Transaccion
import com.google.android.material.card.MaterialCardView

class TransaccionAdapter(
    private var transacciones: List<Transaccion>
) : RecyclerView.Adapter<TransaccionAdapter.TransaccionViewHolder>() {

    inner class TransaccionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardIcono: MaterialCardView = view.findViewById(R.id.cardIcono)
        val ivIcono: ImageView = view.findViewById(R.id.ivIconoTransaccion)
        val tvDescripcion: TextView = view.findViewById(R.id.tvDescripcion)
        val tvIdTransaccion: TextView = view.findViewById(R.id.tvIdTransaccion)
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)
        val tvMonto: TextView = view.findViewById(R.id.tvMonto)
        val cardTipo: MaterialCardView = view.findViewById(R.id.cardTipo)
        val tvTipo: TextView = view.findViewById(R.id.tvTipo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransaccionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaccion, parent, false)
        return TransaccionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransaccionViewHolder, position: Int) {
        val transaccion = transacciones[position]
        val context = holder.itemView.context

        // Descripción
        holder.tvDescripcion.text = transaccion.descripcion

        // ID de transacción
        holder.tvIdTransaccion.text = "Transaction ID ${transaccion.id}"

        // Fecha
        holder.tvFecha.text = transaccion.fechaSoloFecha()

        // Monto con formato
        holder.tvMonto.text = transaccion.montoFormateado()

        // Tipo
        holder.tvTipo.text = transaccion.tipo

        // Colores según el tipo de transacción
        if (transaccion.esIngreso()) {
            // Ingreso (Crédito) - Verde
            holder.tvMonto.setTextColor(ContextCompat.getColor(context, R.color.green_success))
            holder.cardTipo.setCardBackgroundColor(ContextCompat.getColor(context, R.color.green_success))
            holder.cardIcono.setCardBackgroundColor(ContextCompat.getColor(context, R.color.green_light))
            holder.ivIcono.setColorFilter(ContextCompat.getColor(context, R.color.green_success))
        } else {
            // Egreso (Débito) - Rojo
            holder.tvMonto.setTextColor(ContextCompat.getColor(context, R.color.red_error))
            holder.cardTipo.setCardBackgroundColor(ContextCompat.getColor(context, R.color.red_error))
            holder.cardIcono.setCardBackgroundColor(ContextCompat.getColor(context, R.color.red_light))
            holder.ivIcono.setColorFilter(ContextCompat.getColor(context, R.color.red_error))
        }
    }

    override fun getItemCount(): Int = transacciones.size

    fun actualizarTransacciones(nuevasTransacciones: List<Transaccion>) {
        transacciones = nuevasTransacciones
        notifyDataSetChanged()
    }
}