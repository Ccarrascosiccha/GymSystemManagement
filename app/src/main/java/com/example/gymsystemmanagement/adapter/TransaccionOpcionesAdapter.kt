package com.example.gymsystemmanagement.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.gymsystemmanagement.R
import com.example.gymsystemmanagement.entity.Transaccion
import com.google.android.material.button.MaterialButton

class TransaccionOpcionesAdapter(
    private var transacciones: List<Transaccion>,
    private val onVerDetalles: (Transaccion) -> Unit,
    private val onEliminar: (Transaccion) -> Unit
) : RecyclerView.Adapter<TransaccionOpcionesAdapter.TransaccionViewHolder>() {

    inner class TransaccionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvIdTransaccion: TextView = view.findViewById(R.id.tvIdTransaccion)
        val tvDescripcion: TextView = view.findViewById(R.id.tvDescripcion)
        val tvTipoBadge: TextView = view.findViewById(R.id.tvTipoBadge)
        val tvMonto: TextView = view.findViewById(R.id.tvMonto)
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)
        val tvHora: TextView = view.findViewById(R.id.tvHora)
        val tvMetodoPago: TextView = view.findViewById(R.id.tvMetodoPago)
        val layoutMetodoPago: LinearLayout = view.findViewById(R.id.layoutMetodoPago)
        val ivIconoMonto: ImageView = view.findViewById(R.id.ivIconoMonto)
        val btnVerDetalles: MaterialButton = view.findViewById(R.id.btnVerDetalles)
        val btnEliminar: MaterialButton = view.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransaccionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaccion_opciones, parent, false)
        return TransaccionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransaccionViewHolder, position: Int) {
        val transaccion = transacciones[position]
        val context = holder.itemView.context

        // ID
        holder.tvIdTransaccion.text = transaccion.id.toString()

        // Descripción
        holder.tvDescripcion.text = transaccion.descripcion

        // Fecha y hora
        holder.tvFecha.text = transaccion.fechaSoloFecha()
        holder.tvHora.text = " - ${transaccion.fechaSoloFecha()}"

        // Monto
        holder.tvMonto.text = transaccion.montoFormateado()

        // Extraer método de pago de la descripción (si existe)
        val metodoPago = extraerMetodoPago(transaccion.descripcion)
        if (metodoPago != null) {
            holder.layoutMetodoPago.visibility = View.VISIBLE
            holder.tvMetodoPago.text = metodoPago
        } else {
            holder.layoutMetodoPago.visibility = View.GONE
        }

        // Configurar colores según tipo
        if (transaccion.esIngreso()) {
            // Ingreso - Verde
            holder.tvTipoBadge.text = "Ingreso"
            holder.tvTipoBadge.setBackgroundResource(R.drawable.bg_estado_activo)
            holder.tvMonto.setTextColor(ContextCompat.getColor(context, R.color.green_success))
            holder.ivIconoMonto.setColorFilter(ContextCompat.getColor(context, R.color.green_success))
        } else {
            // Egreso - Rojo
            holder.tvTipoBadge.text = "Egreso"
            holder.tvTipoBadge.setBackgroundResource(R.drawable.bg_estado_vencido)
            holder.tvMonto.setTextColor(ContextCompat.getColor(context, R.color.red_error))
            holder.ivIconoMonto.setColorFilter(ContextCompat.getColor(context, R.color.red_error))
        }

        // Listeners de botones
        holder.btnVerDetalles.setOnClickListener {
            onVerDetalles(transaccion)
        }

        holder.btnEliminar.setOnClickListener {
            onEliminar(transaccion)
        }
    }

    override fun getItemCount(): Int = transacciones.size

    fun actualizarTransacciones(nuevasTransacciones: List<Transaccion>) {
        transacciones = nuevasTransacciones
        notifyDataSetChanged()
    }

    private fun extraerMetodoPago(descripcion: String): String? {
        val metodosComunes = listOf("Yape", "Plin", "Efectivo", "Tarjeta", "Transferencia")
        return metodosComunes.find { descripcion.contains(it, ignoreCase = true) }
    }
}